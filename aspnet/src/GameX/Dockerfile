FROM microsoft/aspnet
#RUN yes | certmgr -ssl -m https://nugetgallery.blob.core.windows.net  https://nuget.org https://www.myget.org https://www.myget.org/F/aspnetvnext/api/v2 https://go.microsoft.com https://www.myget.org
#RUN mozroots --import --machine --sync --quiet

RUN mkdir -p ~/.config/NuGet/
RUN curl -o ~/.config/NuGet/NuGet.Config -sSL https://gist.githubusercontent.com/Tazer/1ad0e439846ee26ef9a5/raw/12bc6b02b74af55154e456ec232ef3c01ec960e6/Nuget.config

COPY . /app
WORKDIR /app
RUN ["dnu", "restore"]

EXPOSE 5005
ENTRYPOINT ["dnx", ".", "kestrel"]
