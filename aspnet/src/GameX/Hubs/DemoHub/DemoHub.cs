﻿using System;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.AspNet.SignalR;
using Microsoft.AspNet.SignalR.Hubs;
using SignalRSample.Web.Hubs.DemoHub;

namespace GameX
{
    [HubName("demo")]
    public class DemoHub : Hub
    {
        private static readonly TaskCompletionSource<object> _neverEndingTcs = new TaskCompletionSource<object>();

        private readonly IHubContext<TypedDemoHub, IClient> _typedDemoContext;
        private readonly IPersistentConnectionContext _rawConnectionContext;

        public DemoHub(
            IHubContext<TypedDemoHub, IClient> typedDemoContext,
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

        public void SendToUser(string userId)
        {
            Clients.User(userId).invoke();
        }

        public void AddToGroups()
        {
            Groups.Add(Context.ConnectionId, "foo");
            Groups.Add(Context.ConnectionId, "bar");
            Clients.Caller.groupAdded();
        }

        public void DoSomethingAndCallError()
        {
            Clients.Caller.errorInCallback();
        }

        public Task DynamicTask()
        {
            return Clients.All.signal(Guid.NewGuid());
        }

        public async Task PlainTask()
        {
            await Task.Delay(500);
        }

        public async Task<int> GenericTaskWithContinueWith()
        {
            return await Task.Run(() => 2 + 2).ContinueWith(task => task.Result);
        }

        public async Task TaskWithException()
        {
            await Task.Factory.StartNew(() =>
            {
                throw new Exception();
            });
        }

        public async Task<int> GenericTaskWithException()
        {
            return await Task<int>.Factory.StartNew(() =>
            {
                throw new Exception();
            });
        }

        public void SynchronousException()
        {
            throw new Exception();
        }

        public void HubException()
        {
            throw new HubException("message", "errorData");
        }

        public void HubExceptionWithoutErrorData()
        {
            throw new HubException("message");
        }

        public Task CancelledTask()
        {
            var tcs = new TaskCompletionSource<object>();
            tcs.SetCanceled();
            return tcs.Task;
        }

        public Task<int> CancelledGenericTask()
        {
            var tcs = new TaskCompletionSource<int>();
            return Task.Factory.StartNew(() =>
            {
                tcs.SetCanceled();
                return tcs.Task;
            }).Unwrap();
        }

        public Task NeverEndingTask()
        {
            return _neverEndingTcs.Task;
        }

        public void SimpleArray(int[] nums)
        {
            foreach (var n in nums)
            {
            }
        }

        public string ReadStateValue()
        {
            return Clients.Caller.name;
        }

        public string SetStateValue(string value)
        {
            Clients.Caller.Company = value;

            return Clients.Caller.Company;
        }

        public object ReadAnyState()
        {
            Clients.Caller.state2 = Clients.Caller.state;
            Clients.Caller.addy = Clients.Caller.state.Address;

            string name = Clients.Caller.state["Name"];
            string street = Clients.Caller.state["Address"]["Street"];

            string dname = Clients.Caller.state.Name;
            string dstreet = Clients.Caller.state.Address.Street;

            if (!name.Equals(dname))
            {
                throw new InvalidOperationException("Fail");
            }

            if (!street.Equals(dstreet))
            {
                throw new InvalidOperationException("Fail");
            }

            return Clients.Caller.state;
        }

        public void ComplexArray(Person[] people)
        {

        }

        public Person ComplexType(Person p)
        {
            Clients.Caller.person = p;
            return p;
        }

        public int PassingDynamicComplex(dynamic p)
        {
            return p.Age;
        }

        public void MultipleCalls()
        {
            for (int i = 0; i < 10; i++)
            {
                Clients.Caller.index = i + 1;
                Clients.Caller.invoke(i);
                Thread.Sleep(1000);
            }
        }

        public async Task<string> ReportProgress(string jobName, IProgress<int> progress)
        {
            for (int i = 0; i <= 100; i += 10)
            {
                await Task.Delay(250);
                progress.Report(i);
            }
            return String.Format("{0} done!", jobName);
        }

        public void Overload()
        {

        }

        public int Overload(int n)
        {
            return n;
        }

        public string InlineScriptTag()
        {
            return "WAITING for Script Tag to replace this.<script>$(\"#inlineScriptTag\").html('Success! Replaced by inline Script Tag');</script>";
        }

        public void UnsupportedOverload(string x)
        {

        }

        public void UnsupportedOverload(int x)
        {

        }

        public void TestGuid()
        {
            Clients.Caller.TestGuid(new Guid());
        }

        public void DynamicInvoke(string method)
        {
            IClientProxy proxy = Clients.Caller;
            proxy.Invoke(method);
        }

        public void MispelledClientMethod()
        {
            Clients.Caller.clientMethd();
        }

        public void InvokeViaInjectedContext(int arg1, int arg2)
        {
            _typedDemoContext.Clients.Client(Context.ConnectionId).MethodB(arg1, arg2);
        }

        public string ReturnLargePayload()
        {
            return new string('a', 64 * 1024);
        }

        public class Person
        {
            public string Name { get; set; }
            public int Age { get; set; }
            public Address Address { get; set; }
        }

        public class Address
        {
            public string Street { get; set; }
            public string Zip { get; set; }
        }
    }
}