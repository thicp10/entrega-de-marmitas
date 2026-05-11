# Entrega de Marmitas - CRUD de Clientes

Projeto Spring Boot para gerenciamento de clientes de entrega de marmitas com banco de dados PostgreSQL e exportação para Excel.

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Maven**
- **PostgreSQL**
- **Apache POI** (para exportação Excel)
- **Spring Data JPA**

## Estrutura do Projeto

```
src/main/java/com/marmitas/entregademarmitas/
├── controller/
│   └── ClienteController.java
├── service/
│   ├── ClienteService.java
│   └── ExcelExportService.java
├── repository/
│   └── ClienteRepository.java
├── model/
│   └── Cliente.java
└── EntregaDeMarmitasApplication.java
```

## Configuração do Banco de Dados

1. Crie um banco de dados PostgreSQL:
```sql
CREATE DATABASE entrega_marmitas;
```

2. Configure as credenciais no arquivo `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/entrega_marmitas
spring.datasource.username=postgres
spring.datasource.password=sua_senha
```

## Campos do Cliente

- **id**: Identificador único (auto gerado)
- **nome**: Nome do cliente (obrigatório)
- **endereco**: Endereço do cliente (obrigatório)
- **rg**: RG do cliente (opcional, único)
- **data**: Data do cadastro/entrega (obrigatório)
- **recebeu**: Status se recebeu a marmita (obrigatório)

## Endpoints da API

### Operações CRUD

- `GET /api/clientes` - Listar todos os clientes
- `GET /api/clientes/{id}` - Buscar cliente por ID
- `POST /api/clientes` - Criar novo cliente
- `PUT /api/clientes/{id}` - Atualizar cliente existente
- `DELETE /api/clientes/{id}` - Excluir cliente

### Buscas Avançadas

- `GET /api/clientes/buscar/nome?nome={texto}` - Buscar por nome (case insensitive)
- `GET /api/clientes/buscar/rg?rg={rg}` - Buscar por RG
- `GET /api/clientes/buscar/recebeu?recebeu={true/false}` - Filtrar por status de recebimento
- `GET /api/clientes/buscar/data?dataInicio={YYYY-MM-DD}&dataFim={YYYY-MM-DD}` - Buscar por período

### Estatísticas

- `GET /api/clientes/estatisticas/total` - Total de clientes
- `GET /api/clientes/estatisticas/receberam` - Total que receberam
- `GET /api/clientes/estatisticas/nao-receberam` - Total que não receberam

### Exportação

- `GET /api/clientes/exportar/excel` - Exportar todos os clientes para Excel

## Exemplos de Uso

### Criar Cliente
```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "endereco": "Rua das Flores, 123",
    "rg": "123456789",
    "data": "2024-01-15",
    "recebeu": true
  }'
```

### Listar Todos
```bash
curl http://localhost:8080/api/clientes
```

### Exportar para Excel
```bash
curl -O http://localhost:8080/api/clientes/exportar/excel
```

## Como Executar

1. **Pré-requisitos:**
   - Java 17 ou superior
   - Maven 3.6 ou superior
   - PostgreSQL

2. **Compilar e executar:**
```bash
mvn clean install
mvn spring-boot:run
```

3. **Acessar a API:**
   - A API estará disponível em `http://localhost:8080`

## Estrutura do Excel Exportado

O arquivo Excel gerado contém:
- Cabeçalho formatado com os campos
- Dados dos clientes
- Estatísticas no rodapé (total, receberam, não receberam)
- Formatação de bordas e cores para melhor visualização

## Validações

- Nome e endereço são obrigatórios
- Data é obrigatória
- Status "recebeu" é obrigatório
- RG é opcional, mas se informado deve ser único

## Licença

MIT
