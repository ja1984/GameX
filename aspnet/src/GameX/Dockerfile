FROM microsoft/aspnet:coreclr-1.0.0-beta5-11624

COPY . /app
WORKDIR /app
RUN ["dnu", "restore"]

EXPOSE 5005
ENTRYPOINT ["dnx", ".", "kestrel"]
