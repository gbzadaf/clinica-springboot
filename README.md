#  Clínica API — Sistema de Agendamento Médico

API REST para gerenciamento de consultas médicas, desenvolvida com Java 21 e Spring Boot 3.

---

##  Tecnologias

| Tecnologia | Uso |
|---|---|
| Java 21 | Linguagem principal |
| Spring Boot 3.3 | Framework base |
| Spring Security + JWT | Autenticação stateless |
| Spring Data JPA + PostgreSQL | Persistência de dados |
| JavaMailSender + Mailtrap | Notificações por e-mail |
| SpringDoc OpenAPI | Documentação interativa |
| JUnit 5 + Mockito | Testes unitários |

---

##  Funcionalidades

- Autenticação com JWT e controle de acesso por roles (`ADMIN`, `DOCTOR`, `PATIENT`)
- CRUD de pacientes e médicos com soft delete
- Agendamento de consultas com verificação de conflito de horário
- Cancelamento com regra de 24h de antecedência
- Notificações por e-mail assíncronas ao agendar e cancelar consultas
- Paginação em todas as listagens
- Tratamento global de exceções com respostas padronizadas
- Documentação interativa via Swagger UI
- Versionamento de API com prefixo `/v1`

---

##  Arquitetura

```
src/main/java/com/gabrielf/clinica/
├── config/          # Configurações de segurança e OpenAPI
├── controller/      # Controllers REST versionados em /v1
├── dto/             # Records de request e response
│   ├── request/
│   └── response/
├── exception/       # Exceções customizadas e GlobalExceptionHandler
├── model/           # Entidades JPA
│   └── enums/
├── repository/      # Interfaces Spring Data JPA
├── security/        # Filtro JWT e UserDetailsService
└── service/         # Regras de negócio
```

---

##  Pré-requisitos

- Java 21
- PostgreSQL 16
- Maven 3.9+

---

##  Configuração e execução

**1. Clone o repositório:**
```bash
git clone https://github.com/seu-usuario/clinica-api.git
cd clinica-api
```

**2. Crie o banco de dados:**
```sql
CREATE DATABASE clinica_db;
```

**3. Configure as variáveis de ambiente:**

| Variável | Descrição |
|---|---|
| `DATABASE_USERNAME` | Usuário do PostgreSQL |
| `DATABASE_PASSWORD` | Senha do PostgreSQL |
| `JWT_SECRET` | Chave secreta para assinar o JWT (mín. 256 bits em Base64) |
| `JWT_EXPIRATION` | Tempo de expiração do token em ms (ex: `86400000` = 24h) |
| `MAILTRAP_USERNAME` | Usuário SMTP do Mailtrap |
| `MAILTRAP_PASSWORD` | Senha SMTP do Mailtrap |

**4. Rode a aplicação:**
```bash
./mvnw spring-boot:run
```

**5. Acesse a documentação:**
```
http://localhost:8080/swagger-ui/index.html
```

---

##  Autenticação

Todos os endpoints, exceto `/v1/auth/**`, requerem autenticação via JWT.

**Registrar usuário:**
```http
POST /v1/auth/register
Content-Type: application/json

{
  "name": "Admin",
  "email": "admin@clinica.com",
  "password": "123456",
  "role": "ADMIN"
}
```

**Login:**
```http
POST /v1/auth/login
Content-Type: application/json

{
  "email": "admin@clinica.com",
  "password": "123456"
}
```

Use o token retornado no header de todas as requisições seguintes:
```
Authorization: Bearer {token}
```

---

##  Endpoints

### Autenticação
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/v1/auth/register` | Registra um novo usuário |
| `POST` | `/v1/auth/login` | Autentica e retorna o JWT |

### Pacientes
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/v1/patients` | Lista pacientes (paginado) |
| `POST` | `/v1/patients` | Cadastra paciente |
| `GET` | `/v1/patients/{id}` | Busca paciente por ID |
| `PUT` | `/v1/patients/{id}` | Atualiza paciente |
| `DELETE` | `/v1/patients/{id}` | Inativa paciente (soft delete) |

### Médicos
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/v1/doctors` | Lista médicos (paginado) |
| `POST` | `/v1/doctors` | Cadastra médico |
| `GET` | `/v1/doctors/{id}` | Busca médico por ID |
| `GET` | `/v1/doctors/specialty/{specialty}` | Busca médicos por especialidade |
| `PUT` | `/v1/doctors/{id}` | Atualiza médico |
| `DELETE` | `/v1/doctors/{id}` | Inativa médico (soft delete) |

### Agendamentos
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/v1/appointments` | Cria agendamento |
| `GET` | `/v1/appointments` | Lista agendamentos (paginado) |
| `GET` | `/v1/appointments/{id}` | Busca agendamento por ID |
| `GET` | `/v1/appointments/patient/{id}` | Agendamentos de um paciente |
| `GET` | `/v1/appointments/doctor/{id}` | Agendamentos de um médico |
| `PATCH` | `/v1/appointments/{id}/cancel` | Cancela agendamento |
| `PATCH` | `/v1/appointments/{id}/confirm` | Confirma agendamento |

---

##  Testes

```bash
./mvnw test
```

Cobertura de testes unitários com JUnit 5 e Mockito nos services principais (`PatientService` e `AppointmentService`), incluindo cenários de sucesso e de erro.

---

##  Decisões de Arquitetura

**UUID como ID** — mais seguro que `Long` sequencial, evita enumeração de recursos via URL.

**Soft delete** — pacientes e médicos nunca são removidos do banco. O campo `active = false` preserva o histórico de consultas vinculadas.

**`@Async` nas notificações** — o envio de e-mail ocorre em thread separada, sem bloquear a resposta da API.

**Records nos DTOs** — imutabilidade garantida e código mais enxuto, aproveitando os recursos do Java 21.

**Versionamento de API** — prefixo `/v1` permite evoluir a API sem quebrar clientes existentes.

**GlobalExceptionHandler** — respostas de erro padronizadas em toda a aplicação, com campos `status`, `error`, `message`, `timestamp` e `details`.

**Separação de camadas** — controllers não contêm lógica de negócio; toda a regra fica nos services, facilitando testes e manutenção.
