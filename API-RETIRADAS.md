# API de Retirada de Marmitas

## Endpoints Disponíveis

### 1. Registrar Retirada de Marmita
**POST** `/api/retiradas/registrar`

Valida e registra a retirada de marmita para um cliente existente.

**Body (JSON):**
```json
{
  "codigo": "123",
  "nome": "João Silva",
  "rg": "123456789"
}
```

**Observações:**
- Apenas um dos campos é necessário: `codigo`, `nome` ou `rg`
- `codigo`: ID numérico do cliente
- `nome`: Nome completo do cliente (busca por correspondência)
- `rg`: RG do cliente

**Respostas:**
- **200 OK**: Retirada registrada com sucesso
```json
{
  "mensagem": "Retirada registrada com sucesso",
  "id_retirada": 1,
  "nome_cliente": "João Silva",
  "codigo_cliente": 123,
  "data_retirada": "2026-04-28T18:35:00"
}
```

- **400 Bad Request**: Parâmetros inválidos
- **404 Not Found**: Cliente não encontrado
- **500 Internal Server Error**: Erro no servidor

### 2. Listar Todas as Retiradas
**GET** `/api/retiradas`

Retorna todas as retiradas registradas no sistema.

**Resposta:**
```json
[
  {
    "id": 1,
    "cliente": {
      "id": 123,
      "nome": "João Silva"
    },
    "dataRetirada": "2026-04-28T18:35:00"
  }
]
```

### 3. Listar Retiradas por Cliente
**GET** `/api/retiradas/cliente/{clienteId}`

Retorna todas as retiradas de um cliente específico.

**Parâmetros:**
- `clienteId`: ID do cliente

### 4. Listar Retiradas por Período
**GET** `/api/retiradas/periodo?dataInicio=2026-04-01T00:00:00&dataFim=2026-04-30T23:59:59`

Retorna retiradas dentro de um período específico.

**Parâmetros:**
- `dataInicio`: Data/hora inicial (ISO 8601)
- `dataFim`: Data/hora final (ISO 8601)

### 5. Estatísticas
**GET** `/api/retiradas/total`

Retorna estatísticas das retiradas.

**Resposta:**
```json
{
  "total_retiradas": 45
}
```

### 6. Exportar para Excel
**GET** `/api/retiradas/exportar/excel`

Gera e baixa uma planilha Excel com as retiradas.

**Parâmetros Opcionais:**
- `dataInicio`: Filtrar por período inicial
- `dataFim`: Filtrar por período final

**Exemplos:**
- Exportar todas: `/api/retiradas/exportar/excel`
- Exportar por período: `/api/retiradas/exportar/excel?dataInicio=2026-04-01T00:00:00&dataFim=2026-04-30T23:59:59`

**Resposta:**
- Download do arquivo `.xlsx` com nome: `retiradas_marmitas_YYYYMMDD_HHMMSS.xlsx`

**Conteúdo da Planilha:**
- ID Retirada
- Nome Cliente
- Código Cliente
- Data/Hora Retirada
- Estatísticas no rodapé

## Exemplos de Uso

### Registrar retirada por código
```bash
curl -X POST http://localhost:8080/api/retiradas/registrar \
  -H "Content-Type: application/json" \
  -d '{"codigo": "123"}'
```

### Registrar retirada por nome
```bash
curl -X POST http://localhost:8080/api/retiradas/registrar \
  -H "Content-Type: application/json" \
  -d '{"nome": "João Silva"}'
```

### Registrar retirada por RG
```bash
curl -X POST http://localhost:8080/api/retiradas/registrar \
  -H "Content-Type: application/json" \
  -d '{"rg": "123456789"}'
```

### Exportar retiradas para Excel
```bash
curl -X GET http://localhost:8080/api/retiradas/exportar/excel \
  --output retiradas.xlsx
```

## Validações

- Apenas clientes cadastrados podem realizar retiradas
- Se múltiplos clientes forem encontrados pelo nome, será retornado erro solicitando nome completo ou código
- Todos os erros retornam mensagens amigáveis em português
- Data/hora da retirada é registrada automaticamente no momento do registro
