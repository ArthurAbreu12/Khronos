# Khronos — Aplicativo Desktop (Java + JavaFX + PostgreSQL)

Este é o **app** de verdade (janela nativa, sem navegador), construído com a
mesma stack citada no relatório: **Java** para toda a lógica, **SceneBuilder /
FXML** para a interface e **SQL (PostgreSQL)** para persistência.

## Estrutura

```
khronos-app/
├── pom.xml
├── src/main/java/com/khronos/
│   ├── MainApp.java                  (ponto de entrada JavaFX)
│   ├── controller/MainController.java (toda a lógica da tela)
│   ├── model/                        (Project, Task, TimeEntry — POJOs simples)
│   ├── dao/                          (acesso direto ao banco via JDBC/SQL puro)
│   └── db/Database.java              (conexão JDBC)
├── src/main/resources/
│   ├── db.properties                 (credenciais do PostgreSQL)
│   └── com/khronos/
│       ├── main.fxml                 (tela — pode abrir e editar no SceneBuilder)
│       └── style.css                 (identidade visual do leopardo)
└── sql/
    ├── schema.sql   (criação das tabelas)
    └── seed.sql     (dados de exemplo, opcional)
```

## Pré-requisitos

- Java 17 ou superior (JDK, não só JRE)
- Maven 3.9+
- PostgreSQL 14+ rodando localmente
- (Opcional) [SceneBuilder](https://gluonhq.com/products/scene-builder/) para
  editar `main.fxml` visualmente, exatamente como descrito na ferramenta
  citada no relatório

## 1. Criar o banco de dados

```sql
CREATE DATABASE khronos;
```

Depois rode o schema (as tabelas **não** são criadas automaticamente neste
app — ao contrário da versão backend web, aqui o app já assume que o banco
existe):

```bash
psql -U postgres -d khronos -f sql/schema.sql
psql -U postgres -d khronos -f sql/seed.sql   # opcional, dados de teste
```

## 2. Configurar a conexão

Edite `src/main/resources/db.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/khronos
db.user=postgres
db.password=postgres
```

## 3. Rodar o app

```bash
cd khronos-app
mvn javafx:run
```

Isso abre a janela do Khronos diretamente no seu computador — nada de
navegador, é um aplicativo desktop mesmo.

Se preferir abrir no IntelliJ ou Eclipse, importe como projeto Maven e rode a
classe `com.khronos.MainApp` (configure as VM options do JavaFX se a sua IDE
pedir, ou apenas use o goal `javafx:run` pelo terminal integrado).

## 4. Editando a tela no SceneBuilder

Abra `src/main/resources/com/khronos/main.fxml` direto no SceneBuilder — ele
já usa `fx:controller="com.khronos.controller.MainController"`, então os
campos (`fx:id`) já batem com o código Java. Qualquer alteração visual feita
lá refletirá automaticamente ao rodar o app novamente.

## O que o app faz

- **Cronômetro**: escolhe projeto → tarefa → inicia e para o cronômetro,
  salvando o registro direto no PostgreSQL
- **Projetos & Tarefas**: cria projetos e tarefas, com separação clara entre
  eles (o diferencial citado no referencial teórico do TCC)
- **Relatórios**: barras de progresso com o total de horas por projeto
- **Histórico**: tabela com os últimos apontamentos salvos

## Próximos passos sugeridos para o TCC

- Tela de login e tabela `users`, cobrindo a "admissão e demissão de
  funcionários" citada no referencial teórico
- Empacotar como executável nativo com `jpackage` (gera `.exe`/`.dmg`/`.deb`)
  para instalar sem precisar de Java/Maven na máquina do usuário final
- Migrar `schema.sql` para uma ferramenta de versionamento de banco (Flyway)
