using System;
using System.Collections;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.AspNet.SignalR;
using Microsoft.AspNet.SignalR.Hubs;
using SignalRSample.Web.Hubs.DemoHub;

namespace GameX.Hubs.GameHub
{
    [HubName("game")]
    public class GameHub : Hub
    {
      
        private readonly IPersistentConnectionContext _rawConnectionContext;
        private readonly static World _world = new World(true);

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
            return _world.Countries[1];
        }

        public void Kill()
        {
            _world.Countries[1].Population--;
            Clients.All.updateCountry(_world.Countries[1]);
        }

    }

    public class World
    {
        public World(bool fakeData = false)
        {
            if (fakeData)
            {
                Countries = new List<Country>
                {
                    new Country() {Name = "Sweden", Population = 9300000},
                    new Country() {Name = "Small Country", Population = 100}
                };
            }
        }

        public IList<Country> Countries { get; set; }

    }

    public class Country
    {
        public string Name { get; set; }
        public long Population { get; set; }
    }
}