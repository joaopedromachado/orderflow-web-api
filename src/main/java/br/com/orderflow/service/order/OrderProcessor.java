package br.com.orderflow.service.order;

import br.com.orderflow.domain.order.Order;
import br.com.orderflow.domain.order.OrderStatus;
import br.com.orderflow.service.OrderReportTemplateRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class OrderProcessor {

    private final OrderService orderService;
    private final OrderReportTemplateRenderer renderer;
    private final S3Client s3Client;
    private final String bucketName;

    public OrderProcessor(final OrderService orderService,
                          final OrderReportTemplateRenderer renderer,
                          final S3Client s3Client,
                          @Value("${aws.bucket.name}") final String bucketName) {
        this.orderService = orderService;
        this.renderer = renderer;
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public void process() {
        final List<Order> orders = orderService.getAllByOrderStatus(OrderStatus.RECEIVED);

        orders.forEach(this::saveReportInS3);
    }

    private void saveReportInS3(final Order order) {
        final String report = this.renderer.buildTemplateBy(order);
        final String reportKey = "reports/orders/" + order.getOrderId() + ".html";
        final PutObjectRequest request = PutObjectRequest.builder()
                .bucket(this.bucketName)
                .key(reportKey)
                .contentType("text/html; charset=UTF-8")
                .build();

        this.s3Client.putObject(request, RequestBody.fromString(report, StandardCharsets.UTF_8));
    }

}
