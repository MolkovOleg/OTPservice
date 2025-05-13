
###############################################################################
# ЭТАП 1 ‑ builder: собираем fat-jar внутри контейнера Maven
###############################################################################
FROM maven:3.9-eclipse-temurin-17 AS builder

# каталоги
WORKDIR /workspace

# сначала копируем лишь pom.xml и скачиваем зависимости
COPY pom.xml .
RUN mvn -B dependency:go-offline

# затем копируем остальной исходный код и собираем artefact
COPY src ./src
RUN mvn -B package -DskipTests

###############################################################################
# ЭТАП 2 ‑ runtime: минимальный JRE-образ
###############################################################################
FROM eclipse-temurin:17-jre-jammy AS runtime

# Некритично, но лучше запускать под непривилегированным пользователем
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring

WORKDIR /app

# копируем собранный jar с предыдущего этапа
COPY --from=builder /workspace/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
