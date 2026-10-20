package br.com.orderflow.service.order;

import br.com.orderflow.config.ReportTemplateConfig;
import br.com.orderflow.domain.order.Order;
import br.com.orderflow.domain.order.OrderItem;
import br.com.orderflow.domain.order.OrderStatus;
import br.com.orderflow.domain.product.Product;
import br.com.orderflow.domain.user.User;
import br.com.orderflow.service.OrderReportTemplateRenderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.Context;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderProcessorTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OrderReportTemplateRenderer renderer;

    @Mock
    private S3Client s3Client;

    @Test
    void deveRenderizarTemplateDeRelatorioComContextoInformado() {
        final OrderReportTemplateRenderer orderReportTemplateRenderer = new OrderReportTemplateRenderer(
                new ReportTemplateConfig().reportTemplateEngine(),
                "target/test-reports"
        );
        final Context context = new Context(new Locale("pt", "BR"));

        context.setVariable("report", Map.of(
                "generatedAt", "20/10/2026 às 10:00",
                "orderNumber", "#A1B2C3",
                "status", "RECEBIDO",
                "createdAt", "20/10/2026 às 09:30",
                "customerName", "Ana Silva",
                "customerEmail", "ana@example.com",
                "items", List.of(Map.of(
                        "productName", "Produto de teste",
                        "quantity", 2,
                        "unitPrice", "R$ 10,00",
                        "subtotal", "R$ 20,00"
                )),
                "totalAmount", "R$ 20,00"
        ));

        final String html = orderReportTemplateRenderer.renderReport(context);

        assertThat(html)
                .contains("#A1B2C3")
                .contains("Ana Silva")
                .contains("Produto de teste")
                .contains("R$ 20,00");
    }

    @Test
    void deveConstruirTemplateComInformacoesDoPedido() {
        final User user = new User.Builder()
                .name("Ana Silva")
                .email("ana@example.com")
                .build();
        final Product product = new Product.Builder()
                .name("Produto de teste")
                .build();
        final Order order = new Order.Builder()
                .orderId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .status(OrderStatus.RECEIVED)
                .createdAt(Instant.parse("2026-10-20T12:30:00Z"))
                .totalAmount(new BigDecimal("20.00"))
                .user(user)
                .build();
        final OrderItem item = new OrderItem.Builder()
                .order(order)
                .product(product)
                .quantity(2)
                .unitPrice(new BigDecimal("10.00"))
                .build();

        order.setItems(List.of(item));

        final OrderReportTemplateRenderer renderer = new OrderReportTemplateRenderer(
                new ReportTemplateConfig().reportTemplateEngine(),
                "target/test-reports"
        );

        final String html = renderer.buildTemplateBy(order);

        assertThat(html)
                .contains("11111111-1111-1111-1111-111111111111")
                .contains("Ana Silva")
                .contains("ana@example.com")
                .contains("Produto de teste")
                .contains("R$")
                .contains("20,00");
    }

    @Test
    void deveSalvarRelatorioHtmlNoDiretorioConfigurado(@TempDir final Path reportsDirectory) throws Exception {
        final Order order = createOrder();
        final OrderReportTemplateRenderer renderer = new OrderReportTemplateRenderer(
                new ReportTemplateConfig().reportTemplateEngine(),
                reportsDirectory.toString()
        );

        final Path reportFile = renderer.saveReport(order);

        assertThat(reportFile)
                .isEqualTo(reportsDirectory.resolve("order-11111111-1111-1111-1111-111111111111.html"));
        assertThat(Files.readString(reportFile))
                .contains("Ana Silva")
                .contains("Produto de teste");
    }

    @Test
    void deveEnviarRelatorioHtmlParaOBucket() {
        final Order order = createOrder();
        final OrderProcessor orderProcessor = new OrderProcessor(
                this.orderService,
                this.renderer,
                this.s3Client,
                "orderflow-receipts-test"
        );
        final ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);

        when(this.orderService.getAllByOrderStatus(OrderStatus.RECEIVED)).thenReturn(List.of(order));
        when(this.renderer.buildTemplateBy(order)).thenReturn("<html>relatório</html>");

        orderProcessor.process();

        verify(this.s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));
        assertThat(requestCaptor.getValue().bucket()).isEqualTo("orderflow-receipts-test");
        assertThat(requestCaptor.getValue().key())
                .isEqualTo("reports/orders/11111111-1111-1111-1111-111111111111.html");
        assertThat(requestCaptor.getValue().contentType()).isEqualTo("text/html; charset=UTF-8");
    }

    private Order createOrder() {
        final User user = new User.Builder()
                .name("Ana Silva")
                .email("ana@example.com")
                .build();
        final Product product = new Product.Builder()
                .name("Produto de teste")
                .build();
        final Order order = new Order.Builder()
                .orderId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .status(OrderStatus.RECEIVED)
                .createdAt(Instant.parse("2026-10-20T12:30:00Z"))
                .totalAmount(new BigDecimal("20.00"))
                .user(user)
                .build();
        final OrderItem item = new OrderItem.Builder()
                .order(order)
                .product(product)
                .quantity(2)
                .unitPrice(new BigDecimal("10.00"))
                .build();

        order.setItems(List.of(item));

        return order;
    }
}
