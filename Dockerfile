FROM gcr.io/distroless/java21-debian12:nonroot
WORKDIR /app
ENV TZ="Europe/Oslo"
COPY target/familie-pdf.jar app.jar
ENV JDK_JAVA_OPTIONS="-XX:MaxRAMPercentage=75"
CMD ["app.jar"]