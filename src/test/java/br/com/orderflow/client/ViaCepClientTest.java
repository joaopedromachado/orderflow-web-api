package br.com.orderflow.client;

import br.com.orderflow.client.exception.AddressProviderUnavailableException;
import br.com.orderflow.client.exception.CepNotFoundException;
import br.com.orderflow.client.exception.InvalidCepException;
import br.com.orderflow.client.response.ViaCepResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ViaCepClientTest {

    private MockRestServiceServer mockServer;
    private ViaCepClient viaCepClient;

    @BeforeEach
    void setUp() {
        final RestClient.Builder restClientBuilder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        viaCepClient = new ViaCepClient(restClientBuilder
                .baseUrl("https://viacep.com.br/ws")
                .build());
    }

    @Test
    @DisplayName("Deve retornar endereço quando CEP válido for encontrado")
    void deveRetornarEndereco_quandoCepValidoForEncontrado() {
        mockServer.expect(requestTo("https://viacep.com.br/ws/01001000/json/"))
                .andRespond(withSuccess("""
                        {
                          "cep": "01001-000",
                          "logradouro": "Praça da Sé",
                          "complemento": "lado ímpar",
                          "bairro": "Sé",
                          "localidade": "São Paulo",
                          "uf": "SP"
                        }
                        """, MediaType.APPLICATION_JSON));

        final ViaCepResponse response = viaCepClient.getAddressByCep("01001-000");

        assertThat(response.logradouro()).isEqualTo("Praça da Sé");
        assertThat(response.localidade()).isEqualTo("São Paulo");
        assertThat(response.uf()).isEqualTo("SP");
        mockServer.verify();
    }

    @Test
    @DisplayName("Deve retornar erro quando CEP possuir formato inválido")
    void deveLancarExcecao_quandoCepEFormatoInvalido() {
        assertThatThrownBy(() -> viaCepClient.getAddressByCep("123"))
                .isInstanceOf(InvalidCepException.class);
    }

    @Test
    @DisplayName("Deve retornar erro quando CEP não for encontrado")
    void deveLancarExcecao_quandoCepNaoEncontrado() {
        mockServer.expect(requestTo("https://viacep.com.br/ws/99999999/json/"))
                .andRespond(withSuccess("{\"erro\": true}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> viaCepClient.getAddressByCep("99999999"))
                .isInstanceOf(CepNotFoundException.class);
        mockServer.verify();
    }

    @Test
    @DisplayName("Deve retornar erro quando serviço de endereço estiver indisponível")
    void deveRetornarErro_quandoHouverIndisponibilidade() {
        mockServer.expect(requestTo("https://viacep.com.br/ws/01001000/json/"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> viaCepClient.getAddressByCep("01001000"))
                .isInstanceOf(AddressProviderUnavailableException.class);
        mockServer.verify();
    }
}
