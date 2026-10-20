package br.com.orderflow.service;

import br.com.orderflow.domain.order.Order;
import br.com.orderflow.domain.order.OrderItem;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Component
public class OrderReportTemplateRenderer {

    private static final String ORDER_REPORT_TEMPLATE = "order-report";
    private static final Locale BRAZILIAN_PORTUGUESE = new Locale("pt", "BR");
    private static final ZoneId BRAZILIAN_TIME_ZONE = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy 'às' HH:mm", BRAZILIAN_PORTUGUESE)
            .withZone(BRAZILIAN_TIME_ZONE);
    private static final String NOT_AVAILABLE = "N/A";

    private final ITemplateEngine reportTemplateEngine;
    private final Path reportsOutputDirectory;

    public OrderReportTemplateRenderer(@Qualifier("reportTemplateEngine") final ITemplateEngine reportTemplateEngine,
                                       @Value("${reports.output-directory}") final String reportsOutputDirectory) {
        this.reportTemplateEngine = reportTemplateEngine;
        this.reportsOutputDirectory = Path.of(reportsOutputDirectory);
    }

    public String renderReport(final IContext context) {
        return this.reportTemplateEngine.process(ORDER_REPORT_TEMPLATE, context);
    }

    public String buildTemplateBy(final Order order) {
        final Context context = new Context(BRAZILIAN_PORTUGUESE);
        final Map<String, Object> report = new HashMap<>();

        report.put("generatedAt", formatDate(Instant.now()));
        report.put("orderNumber", order.getOrderId().toString());
        report.put("status", order.getStatus().name());
        report.put("createdAt", formatDate(order.getCreatedAt()));
        report.put("customerName", Objects.nonNull(order.getUser()) ? order.getUser().getName() : "Sem nome");
        report.put("customerEmail", order.getUser().getEmail());
        report.put("items", buildItems(order.getItems()));
        report.put("totalAmount", formatCurrency(order.getTotalAmount()));

        context.setVariable("report", report);

        return renderReport(context);
    }

    public Path saveReport(final Order order) {
        final Path reportFile = this.reportsOutputDirectory.resolve("order-" + order.getOrderId() + ".html");

        try {
            Files.createDirectories(this.reportsOutputDirectory);
            Files.writeString(
                    reportFile,
                    buildTemplateBy(order),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );

            return reportFile;
        } catch (final IOException exception) {
            throw new UncheckedIOException("Não foi possível salvar o relatório do pedido " + order.getOrderId(), exception);
        }
    }

    private List<Map<String, Object>> buildItems(final List<OrderItem> orderItems) {
        if (orderItems == null) {
            return Collections.emptyList();
        }

        return orderItems.stream()
                .map(this::buildItem)
                .toList();
    }

    private Map<String, Object> buildItem(final OrderItem orderItem) {
        final Map<String, Object> item = new HashMap<>();
        final BigDecimal subtotal = orderItem.getUnitPrice()
                .multiply(BigDecimal.valueOf(orderItem.getQuantity()));

        item.put("productName", orderItem.getProduct().getName());
        item.put("quantity", orderItem.getQuantity());
        item.put("unitPrice", formatCurrency(orderItem.getUnitPrice()));
        item.put("subtotal", formatCurrency(subtotal));

        return item;
    }

    private String formatDate(final Instant date) {
        if (date == null) {
            return NOT_AVAILABLE;
        }

        return DATE_TIME_FORMATTER.format(date);
    }

    private String formatCurrency(final BigDecimal value) {
        if (value == null) {
            return NOT_AVAILABLE;
        }

        return NumberFormat.getCurrencyInstance(BRAZILIAN_PORTUGUESE).format(value);
    }
}
