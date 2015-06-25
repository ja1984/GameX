using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.AspNet.SignalR;
using Microsoft.AspNet.SignalR.Hubs;
using Newtonsoft.Json;
using SignalRSample.Web.Hubs.DemoHub;

namespace GameX.Hubs.GameHub
{
    [HubName("game")]
    public class GameHub : Hub
    {
      
        private readonly IPersistentConnectionContext _rawConnectionContext;
        private readonly static World _world = GetRealWorld();
        private static  int _currentIndex = 0;
        private static World GetRealWorld()
        {
            var wc = new WebClient();
           var jsonData = wc.DownloadString(
                "https://gist.githubusercontent.com/ja1984/a252430c9d01fbed8876/raw/8cb786949c60094613b3ecad5c738f8a69bbe532/gistfile1.json");
            var world = Newtonsoft.Json.JsonConvert.DeserializeObject<World>(jsonData);

            world.Countries = world.Countries.OrderBy(x => x.Population).ToList();

            return world;
        }

        public GameHub(
            IPersistentConnectionContext<RawConnection> rawConnectionContext)
        {
            _rawConnectionContext = rawConnectionContext;
        }

        public override async Task OnConnected()
        {
            await _rawConnectionContext.Connection.Broadcast(new
            {
                type = RawConnection.MessageType.Broadcast.ToString(),
                from = Context.ConnectionId,
                data = "Connected to GameHub!"
            });
        }

        public Country GetActiveCountry()
        {
            return _world.Countries[_currentIndex];
        }

        public void Kill()
        {
            _world.Countries[_currentIndex].CurrentPopulation--;

            if (_world.Countries[_currentIndex].CurrentPopulation < 1)
                _currentIndex++;

            Clients.All.updateCountry(_world.Countries[_currentIndex]);
        }

    }

    public class World
    {
        public World()
        {

        }

        public IList<Country> Countries { get; set; }

    }

    public class Country
    {
        public string Name { get; set; }
        public long Population { get; set; }
        public long CurrentPopulation { get; set; }
    }
}