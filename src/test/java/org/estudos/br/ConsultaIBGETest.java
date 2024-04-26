package org.estudos.br;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ConsultaIBGETest {
    private static final String ESTADOS_API_URL = "https://servicodados.ibge.gov.br/api/v1/localidades/estados/";

    private static final String DISTRITOS_API_URL = "https://servicodados.ibge.gov.br/api/v1/localidades/distritos/";


    @Test
    @DisplayName("Teste para consulta única de um estado")
    public void testConsultarEstado() throws IOException {
        // Arrange
        String uf = "SP"; // Define o estado a ser consultado

        // Act
        String resposta = ConsultaIBGE.consultarEstado(uf); // Chama o método a ser testado

        // Assert
        // Verifica se a resposta não está vazia
        assert !resposta.isEmpty();

        // Verifica se o status code é 200 (OK)
        HttpURLConnection connection = (HttpURLConnection) new URL(ESTADOS_API_URL + uf).openConnection();
        int statusCode = connection.getResponseCode();
        assertEquals(200, statusCode, "O status code da resposta da API deve ser 200 (OK)");
    }

    @ParameterizedTest
    @ValueSource(strings = {"CE", "PB", "SP", "RS", "AM"})  
    @DisplayName("Teste de consulta para mais de um estado")
    public void testConsultarMultiplosEstados(String estado) throws IOException {
        // Chamada para a API que consulta informações do estado
        String response = ConsultaIBGE.consultarEstado(estado);

        // Converte a resposta de String para JSONObject
        JSONObject jsonResponse = new JSONObject(response);

        // Verifica se a sigla do estado na resposta é igual ao esperado
        assertEquals(estado, jsonResponse.getString("sigla"));
    }

    @ParameterizedTest
    @ValueSource(ints = {2904, 2300309, 52014, 2400307, 320002})
    @DisplayName("Teste de consulta para distritos")
    public void testConsultarMultiplosDistritos(int idDistrito) throws IOException {
        // Consulta informações do distrito com os identificadores fornecidos
        String resposta = ConsultaIBGE.consultarDistrito(idDistrito);

        // Verifica se a resposta não está vazia
        assert !resposta.isEmpty() : "A resposta não deve estar vazia para o id do distrito " + idDistrito;

        // Verifica se o status code é 200 (OK)
        HttpURLConnection connection = (HttpURLConnection) new URL(DISTRITOS_API_URL + idDistrito).openConnection();
        int statusCode = connection.getResponseCode();
        assertEquals(200, statusCode, "O status code deve ser 200 (OK) para o id do distrito " + idDistrito);
    }

    @Mock
    private HttpURLConnection connectionMock;

    // JSON de resposta simulada
    private static final String JSON_RESPONSE = "{\"id\":12,\"sigla\":\"AC\",\"nome\":\"Acre\",\"regiao\":{\"id\":1,\"sigla\":\"N\",\"nome\":\"Norte\"}}";    // Método executado antes de cada teste
    @BeforeEach
    public void setup() throws IOException {
        // Inicializa os mocks
        MockitoAnnotations.openMocks(this);

        // Configura o comportamento do mock
        InputStream inputStream = new ByteArrayInputStream(JSON_RESPONSE.getBytes());
        when(connectionMock.getInputStream()).thenReturn(inputStream);
    }

    @Test
    @DisplayName("Consulta usando o Mock")
    public void testConsultarEstadoComMock() throws IOException {
        // Sigla do estado a ser consultado
        String estadoUf = "AC";

        // Act (Execução do método a ser testado)
        String response = ConsultaIBGE.consultarEstado(estadoUf);

        // Verificamos se o JSON retornado é o mesmo que o JSON de resposta simulada
        assertEquals(JSON_RESPONSE, response, "O JSON retornado não corresponde ao esperado.");
    }
}