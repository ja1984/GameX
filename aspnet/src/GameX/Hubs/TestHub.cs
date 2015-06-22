using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using GameX;
using Microsoft.AspNet.SignalR;
using Microsoft.AspNet.SignalR.Hubs;

namespace Twee.Api.Hubs
{
    [HubName("test")]
    public class TestHub : Hub
    {
        private static readonly TaskCompletionSource<object> _neverEndingTcs = new TaskCompletionSource<object>();
        private readonly IHubContext<TypedTestHub, IClient> _typedDemoContext;
        private readonly IPersistentConnectionContext _rawConnectionContext;

        public TestHub(IHubContext<TypedTestHub, IClient> typedDemoContext,
            IPersistentConnectionContext<RawConnection> rawConnectionContext)
        {
            _typedDemoContext = typedDemoContext;
            _rawConnectionContext = rawConnectionContext;
        }

        public override async Task OnConnected()
        {
            await _rawConnectionContext.Connection.Broadcast(new
            {
                type = RawConnection.MessageType.Broadcast.ToString(),
                from = Context.ConnectionId,
                data = "Connected to DemoHub!"
            });
        }

        public Task<int> GetValue()
        {
            return Task.Factory.StartNew(() =>
            {
                Thread.Sleep(5000);
                return 10;
            });
        }

    }
}
