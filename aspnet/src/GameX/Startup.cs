using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Threading.Tasks;
using Microsoft.AspNet.Builder;
using Microsoft.AspNet.Http;
using Microsoft.AspNet.SignalR;
using Microsoft.Framework.DependencyInjection;
using Microsoft.AspNet.WebSockets.Server;

namespace GameX
{
    public class Startup
    {
        // For more information on how to configure your application, visit http://go.microsoft.com/fwlink/?LinkID=398940
        public void ConfigureServices(IServiceCollection services)
        {
              services.AddSignalR(options =>
            {
                options.Hubs.EnableDetailedErrors = true;
            });
   
        }

        public void Configure(IApplicationBuilder app)
        {
            app.UseStaticFiles();
            app.UseSignalR<RawConnection>("/raw-connection");
            app.UseSignalR();
            app.UseWebSockets();
        }
    }
}
