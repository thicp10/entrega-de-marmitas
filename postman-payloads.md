# Payloads para Teste Postman - Entrega de Marmitas

## Configuração Base
- **URL Base**: `http://localhost:8080/api/clientes`
- **Content-Type**: `application/json`

---

## 1. Criar Cliente (POST)

**Endpoint**: `POST /api/clientes`

### Payload 1 - Cliente Completo
```json
{
    "nome": "João Silva",
    "endereco": "Rua das Flores, 123, Bairro Centro, São Paulo - SP",
    "rg": "123456789",
    "data": "2024-01-15",
    "recebeu": true
}
```

### Payload 2 - Cliente sem RG
```json
{
    "nome": "Maria Santos",
    "endereco": "Avenida Principal, 456, Bairro Jardim, Rio de Janeiro - RJ",
    "data": "2024-01-16",
    "recebeu": false
}
```

### Payload 3 - Cliente Básico
```json
{
    "nome": "Pedro Oliveira",
    "endereco": "Rua Verde, 789, Campinas - SP",
    "data": "2024-01-17",
    "recebeu": true
}
```

---

## 2. Atualizar Cliente (PUT)

**Endpoint**: `PUT /api/clientes/{id}`

### Payload de Atualização
```json
{
    "nome": "João Silva da Costa",
    "endereco": "Rua das Flores, 123, Apartamento 101, Bairro Centro, São Paulo - SP",
    "rg": "987654321",
    "data": "2024-01-15",
    "recebeu": true
}
```

---

## 3. Endpoints GET (Sem Payload)

### Listar Todos
```
GET /api/clientes
```

### Buscar por ID
```
GET /api/clientes/1
```

### Buscar por Nome
```
GET /api/clientes/buscar/nome?nome=João
```

### Buscar por RG
```
GET /api/clientes/buscar/rg?rg=123456789
```

### Buscar por Status de Recebimento
```
GET /api/clientes/buscar/recebeu?recebeu=true
```

### Buscar por Período de Data
```
GET /api/clientes/buscar/data?dataInicio=2024-01-15&dataFim=2024-01-20
```

### Estatísticas
```
GET /api/clientes/estatisticas/total
GET /api/clientes/estatisticas/receberam
GET /api/clientes/estatisticas/nao-receberam
```

### Exportar Excel
```
GET /api/clientes/exportar/excel
```

---

## 4. Deletar Cliente (DELETE)

**Endpoint**: `DELETE /api/clientes/{id}`

Sem corpo na requisição.

---

## 5. Exemplos de Respostas

### Resposta de Sucesso (POST/PUT)
```json
{
    "id": 1,
    "nome": "João Silva",
    "endereco": "Rua das Flores, 123, Bairro Centro, São Paulo - SP",
    "rg": "123456789",
    "data": "2024-01-15",
    "recebeu": true
}
```

### Resposta de Lista (GET)
```json
[
    {
        "id": 1,
        "nome": "João Silva",
        "endereco": "Rua das Flores, 123, Bairro Centro, São Paulo - SP",
        "rg": "123456789",
        "data": "2024-01-15",
        "recebeu": true
    },
    {
        "id": 2,
        "nome": "Maria Santos",
        "endereco": "Avenida Principal, 456, Bairro Jardim, Rio de Janeiro - RJ",
        "rg": null,
        "data": "2024-01-16",
        "recebeu": false
    }
]
```

### Resposta de Erro (Validação)
```json
{
    "timestamp": "2024-01-15T10:30:00.000+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Nome é obrigatório"
}
```

### Resposta de Estatísticas
```json
25
```

---

## 6. Coleção Postman

### Importar como Coleção
Você pode importar esta coleção JSON no Postman:

```json
{
    "info": {
        "name": "Entrega de Marmitas API",
        "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
    },
    "item": [
        {
            "name": "Criar Cliente",
            "request": {
                "method": "POST",
                "header": [
                    {
                        "key": "Content-Type",
                        "value": "application/json"
                    }
                ],
                "body": {
                    "mode": "raw",
                    "raw": "{\n    \"nome\": \"João Silva\",\n    \"endereco\": \"Rua das Flores, 123, Bairro Centro, São Paulo - SP\",\n    \"rg\": \"123456789\",\n    \"data\": \"2024-01-15\",\n    \"recebeu\": true\n}"
                },
                "url": {
                    "raw": "http://localhost:8080/api/clientes",
                    "protocol": "http",
                    "host": ["localhost"],
                    "port": "8080",
                    "path": ["api", "clientes"]
                }
            }
        },
        {
            "name": "Listar Todos",
            "request": {
                "method": "GET",
                "url": {
                    "raw": "http://localhost:8080/api/clientes",
                    "protocol": "http",
                    "host": ["localhost"],
                    "port": "8080",
                    "path": ["api", "clientes"]
                }
            }
        },
        {
            "name": "Exportar Excel",
            "request": {
                "method": "GET",
                "url": {
                    "raw": "http://localhost:8080/api/clientes/exportar/excel",
                    "protocol": "http",
                    "host": ["localhost"],
                    "port": "8080",
                    "path": ["api", "clientes", "exportar", "excel"]
                }
            }
        }
    ]
}
```

---

## 7. Dicas para Testes

### Validações Testadas:
- Nome obrigatório (erro 400 se vazio)
- Endereço obrigatório (erro 400 se vazio)
- Data obrigatória (erro 400 se vazia)
- RG único (erro 400 se duplicado)
- ID existente para PUT/DELETE (erro 404 se não encontrado)

### Testes Sugeridos:
1. Criar cliente com todos os campos
2. Criar cliente sem RG (deve funcionar)
3. Tentar criar cliente com nome vazio (deve dar erro)
4. Tentar criar com RG duplicado (deve dar erro)
5. Atualizar cliente existente
6. Tentar atualizar cliente inexistente (deve dar erro)
7. Buscar por diferentes filtros
8. Exportar Excel e verificar o arquivo gerado

### Headers Importantes:
```
Content-Type: application/json
Accept: application/json
```

Para exportação Excel:
```
Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
```
