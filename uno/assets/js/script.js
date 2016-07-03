/**
 * Created by Tom Dobbelaere on 22/06/2016.
 */

var socket;
var host = "digaly.ddns.net";

$(document).ready(function () {
    /*$("#connecting").text("Connecting with " + host + "...");

    socket = io(host + ":3002");

    socket.on("connect", function(){*/
        $("#connecting").hide();
    //});

    loadUsername();
    $("#username").on("keyup", saveUsername);

    /*joinIdentifier = window.location.search.split("=")[1];

    if (typeof(joinIdentifier) != 'undefined') {
        setSelectedType("sun");
        $("#create-game").find("h1").text("Join game")
        $("#create").text("Join").on("click", joinGame);
    } else {
        setSelectedType("moon");
        $("#create").on("click", createGame);
    }*/

    $("#create-game").hide();
});

var saveUsername = function() {
    var username = $("#username").val();

    if (typeof(localStorage) != 'undefined')
    {
        localStorage.username = username;
    }
};

var loadUsername = function() {
    if (typeof(localStorage) != 'undefined')
    {
        if (typeof(localStorage.username) != 'undefined')
        {
            $("#username").val(localStorage.username);
        }
    }
};