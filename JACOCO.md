# Verificação local de cobertura com JaCoCo

## Gerar um relatório completo

```bash
./mvnw clean verify
```

Execute este comando sempre que quiser gerar um relatório novo. Ele remove os artefatos anteriores, compila o projeto, executa os testes e cria os relatórios HTML e XML do JaCoCo.

## Gerar novamente apenas o relatório

```bash
./mvnw jacoco:report
```

Use este comando depois de uma execução de testes que já tenha produzido `target/jacoco.exec`. Ele recria os relatórios sem executar os testes novamente.

Se os testes falharem antes de executar, o relatório não representa a cobertura real do projeto; corrija a falha e execute `./mvnw clean verify` novamente.

## Enviar análise ao SonarCloud

```bash
./mvnw verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.qualitygate.wait=true
```

Execute este comando somente quando a variável de ambiente `SONAR_TOKEN` estiver configurada. Ele gera a cobertura, envia a análise ao SonarCloud e aguarda o resultado do Quality Gate.

## Arquivos gerados

- `target/site/jacoco/index.html`: relatório visual para consulta local.
- `target/jacoco.exec`: dados brutos da execução dos testes.
