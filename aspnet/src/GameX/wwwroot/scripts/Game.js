$(function () {
    var game = $.connection.game;

    var gameViewModel = function (gameConnection) {
        var pub = {};
        var priv = {};

        pub.population = ko.observable(0);
        pub.name = ko.observable('');
        pub.currentPopulation = ko.observable(0);

        pub.kill = function () {
            gameConnection.server.kill();
        };

        pub.init = function() {
            gameConnection.server.getActiveCountry().done(function (country) {
                pub.population(country.Population);
                pub.currentPopulation(country.CurrentPopulation);
                pub.name(country.Name);
            });
        }
        


        gameConnection.client.updateCountry = function (args) {
            pub.population(args.Population);
            pub.currentPopulation(args.CurrentPopulation);
            pub.name(args.Name);
        };

        return pub;
    }

    var vm = gameViewModel(game);

    

    $.connection.hub.logging = true;
    $.connection.hub.start()
        .done(function () {
            vm.init();
        }).fail(function () {
            Console.log("Could not connect!");
        });
    var timeout = null;
    var interval = 10000;
    $.connection.hub.stateChanged(function (change) {
        if (change.newState === $.signalR.connectionState.reconnecting) {
            timeout = setTimeout(function () {
                console.log('Server is unreachable, trying to reconnect...');
            }, interval);
        }
        else if (timeout && change.newState === $.signalR.connectionState.connected) {
            console.log('Server reconnected, reinitialize');
            $.connection.auctionHub.initialize();
            clearTimeout(timeout);
            timeout = null;
        }
    });

    ko.applyBindings(vm);
});