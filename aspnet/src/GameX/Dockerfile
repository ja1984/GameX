FROM microsoft/aspnet

COPY . /app
WORKDIR /app
RUN ["dnu", "restore"]

EXPOSE 5005
ENTRYPOINT ["dnx", ".", "kestrel"]
