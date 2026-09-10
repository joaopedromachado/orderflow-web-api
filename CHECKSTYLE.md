# Checkstyle

O projeto usa Checkstyle para garantir convenções de código em `src/main/java`.
As verificações fazem parte da fase `validate` do Maven, por isso uma violação
interrompe o build antes da compilação e dos testes.

## Regras aplicadas

- Imports explícitos, sem wildcard, sem duplicações e sem imports não usados.
- Ordem de imports: estáticos, aplicação/bibliotecas e `java.*`, com grupos
  separados por uma linha em branco.
- Parâmetros de métodos e construtores declarados como `final` nas camadas de
  aplicação. O pacote `domain/**` é exceção: entidades JPA, setters e builders
  podem usar `final`, mas não são obrigados a isso.
- Indentação de quatro espaços, sem tabs e com espaçamento consistente.
- Arquivos terminados por quebra de linha e separação entre `package` e imports.

## Executar localmente

```bash
./mvnw validate
```

Executa o Checkstyle como parte do ciclo Maven e falha caso encontre violações.

```bash
./mvnw checkstyle:check
```

Executa somente a validação de estilo, útil durante ajustes de formatação e
organização de imports.

```bash
./mvnw verify
```

Executa Checkstyle, compilação, testes e geração de relatórios configurados no
projeto.

## IntelliJ IDEA

1. Instale o plugin **Checkstyle-IDEA**.
2. Abra **Settings → Tools → Checkstyle**.
3. Use **Detect Checkstyle Configuration File**.
4. Ative a configuração `config/checkstyle/checkstyle.xml` e a inspeção
   **CheckStyle**.

O plugin detecta automaticamente esse caminho convencional e mostra violações
diretamente no editor.

As exceções da regra de parâmetros ficam em
`config/checkstyle/suppressions.xml`; o IntelliJ as carrega junto com a
configuração principal.

## Escopo inicial

Os testes em `src/test/java` não são analisados nesta primeira etapa. Isso evita
misturar as convenções de stubs e testes com as regras de produção. O escopo
pode ser ampliado no Maven usando `includeTestSourceDirectory` quando esses
arquivos forem padronizados.
