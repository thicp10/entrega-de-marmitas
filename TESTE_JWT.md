# Guia de Teste - Autenticação JWT

## 1. Iniciar a Aplicação
```bash
mvn spring-boot:run
```

## 2. Fazer Login e Obter Token JWT

### Endpoint de Login
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

### Resposta Esperada
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcxNDg4NzYwMCwiZXhwIjoxNzE0OTc0MDAwfQ.signature",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "role": "ADMIN"
}
```

## 3. Usar o Token nos Endpoints Protegidos

Copie o token da resposta e use no header Authorization:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcxNDg4NzYwMCwiZXhwIjoxNzE0OTc0MDAwfQ.signature
```

## 4. Exemplos de Teste

### 4.1 Listar Clientes
```
GET http://localhost:8080/api/clientes
Authorization: Bearer <seu_token_aqui>
```

### 4.2 Criar Cliente
```
POST http://localhost:8080/api/clientes
Authorization: Bearer <seu_token_aqui>
Content-Type: application/json

{
  "nome": "João Silva",
  "telefone": "(11) 99999-9999",
  "endereco": "Rua das Flores, 123"
}
```

### 4.3 Registrar Retirada
```
POST http://localhost:8080/api/retiradas/registrar
Authorization: Bearer <seu_token_aqui>
Content-Type: application/json

{
  "clienteId": "1",
  "quantidade": "2"
}
```

### 4.4 Listar Retiradas
```
GET http://localhost:8080/api/retiradas
Authorization: Bearer <seu_token_aqui>
```

## 5. Teste com Postman

### 1. Configurar Login
- Method: POST
- URL: http://localhost:8080/api/auth/login
- Body: raw → JSON
```json
{
  "username": "admin",
  "password": "admin123"
}
```

### 2. Configurar Endpoints Protegidos
- Adicionar header: Authorization
- Value: Bearer <token_copiado>

### 3. Salvar o Token
- Copie o token da resposta de login
- Cole no header Authorization dos outros requests

## 6. Teste com curl

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Usar Token
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcxNDg4NzYwMCwiZXhwIjoxNzE0OTc0MDAwfQ.signature"

curl -X GET http://localhost:8080/api/clientes \
  -H "Authorization: Bearer $TOKEN"
```

## 7. Teste com Insomnia

1. **Request de Login:**
   - POST http://localhost:8080/api/auth/login
   - Body → JSON: {"username":"admin","password":"admin123"}
   - Copie o token da resposta

2. **Request Protegido:**
   - Adicionar header: Authorization
   - Value: Bearer <token>

## 8. Validação de Token

O token JWT é válido por 24 horas (86400000 ms). Após esse período, você precisará fazer login novamente.

## 9. Erros Comuns

### 401 Unauthorized
- Token inválido ou expirado
- Header Authorization ausente ou mal formatado

### 403 Forbidden  
- Usuário sem permissão para o recurso

### 400 Bad Request
- Credenciais incorretas no login

## 10. Verificação no Console

Ao iniciar a aplicação, você verá:
```
Usuário admin criado com sucesso! Username: admin, Password: admin123
```

Isso confirma que o usuário admin foi criado no banco de dados.
