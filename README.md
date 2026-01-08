🚀 FlightOnTime System (Hackathon One 2025)

Este projeto é uma plataforma robusta de previsão de atrasos de voos, unindo a performance do Spring Boot (Java), a inteligência de dados do Flask (Python) e a confiabilidade do PostgreSQL.
🛠 Melhorias e Implementações Recentes
🛡️ Monitoramento Avançado com Actuator

Implementamos um Health Check customizado no Spring Boot que monitora a saúde da API Python em tempo real.

    Resiliência: O sistema diferencia se o erro é de rede (DNS), timeout ou se a API está realmente offline.

    Feedback Visual: Integração completa com o /actuator/health, fornecendo detalhes técnicos sobre códigos HTTP e falhas de infraestrutura.

🐳 Dockerização e Orquestração

O projeto foi totalmente containerizado para garantir que rode identicamente em qualquer máquina.

    Rede Privada: Criação de uma bridge network exclusiva para isolamento e comunicação segura entre os serviços.

    Multi-Stage Build: Dockerfiles otimizados que reduzem o tamanho das imagens final, separando o ambiente de build (Maven/Python) do ambiente de execução (JRE/Slim).

    DNS Interno: Configuração de Service Discovery, permitindo que o Java encontre o Python pelo nome do serviço (app-python).

🔌 Integração Híbrida e Inteligente

O sistema foi desenhado para ser Flexível (Híbrido):

    WebClient/HttpConnection: O backend Java consome a API de Machine Learning via requisições assíncronas/otimizadas.

    Profiles Dinâmicos: Suporte a perfis default (rodando localmente no IntelliJ/Terminal) e docker (rodando dentro de containers), ajustando as URLs de conexão automaticamente.

🏗 Arquitetura do Sistema

    spring_app: Núcleo do sistema, gerencia regras de negócio e persistência.

    python_ai_api: Engine de IA que processa as predições de voo.

    postgres_db: Banco de dados relacional para armazenamento de dados históricos.

    frontend: Interface de usuário moderna para interação com o sistema.

🚀 Como Rodar o Projeto
1. Requisitos

    Docker e Docker Compose instalados.

    Git.

2. Rodando via Docker (Recomendado)

Para subir o ecossistema completo (Java + Python + DB + Front):
Bash

# Clone o repositório
git clone <url-do-seu-novo-repo>
cd <pasta-do-projeto>

# Suba todos os serviços
docker-compose up -d --build

Acesse:

    Frontend: http://localhost:3000

    Backend Java: http://localhost:8080

    Health Check: http://localhost:8080/actuator/health

3. Rodando de Forma Híbrida (Desenvolvimento)

Se desejar rodar o Java/Python localmente para debugar:

    Certifique-se de que o Postgres está rodando (via Docker ou Local).

    Configure o application.properties para api.python.url=http://localhost:5000.

    Inicie o Flask: python app.py

    Inicie o Spring: ./mvnw spring-boot:run

🧪 Testes de Integração

O sistema valida a conectividade entre os módulos automaticamente. Se a API Python cair, o Spring Boot detecta em menos de 3 segundos e reporta o estado de degradado, garantindo que o usuário nunca fique sem uma resposta clara do sistema.
