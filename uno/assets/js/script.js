/**
 * Created by Tom Dobbelaere on 22/06/2016.
 */

var socket;
var username;
var joinIdentifier;
var host = "digaly.ddns.net";

$(document).ready(function () {
    $("#connecting").text("Connecting with " + host + "...");

    socket = io(host + ":3005");

    socketBindings();

    loadUsername();
    $("#username").on("keyup", saveUsername);

    joinIdentifier = window.location.search.split("=")[1];

    if (typeof(joinIdentifier) != 'undefined') {
        $("#create-game").find("h1").text("Join game")
        $("#create").text("Join").on("click", joinGame);
    } else {
        $("#create").on("click", createGame);
    }

    $("#link").hide();
    $("#playarea").hide();
    $("#create-game").show();
    $("#chat-message").on('keypress', sendChatMessage);

    $('body').on('click', '.player-bottom .card', playCard).on('click', '#draw-card', drawCard);
});

var playCard = function() {
    var data = + "," + $(this).attr("data-cardvalue");
    socket.emit("play card", JSON.stringify({
        color: $(this).attr("data-cardcolor"),
        value: $(this).attr("data-cardvalue")
    }));
};

var drawCard = function() {
    socket.emit("draw card", "");
};

var saveUsername = function () {
    var username = $("#username").val();

    if (typeof(localStorage) != 'undefined') {
        localStorage.username = username;
    }
};

var loadUsername = function () {
    if (typeof(localStorage) != 'undefined') {
        if (typeof(localStorage.username) != 'undefined') {
            $("#username").val(localStorage.username);
        }
    }
};

var createGame = function (e) {
    e.preventDefault();

    username = $("#username").val();

    socket.emit("create game", JSON.stringify({
        username: username
    }));

    $("#create-game").hide();
};

var joinGame = function (e) {
    e.preventDefault();

    username = $("#username").val();

    socket.emit("join game", JSON.stringify({
        lobbyid: joinIdentifier,
        username: username
    }));

    $("#create-game").hide();
    prepareBoard();
    $("#playarea").show();
};

var readyGame = function (e) {
    e.preventDefault();

    socket.emit("ready game");
};

var prepareBoard = function () {
    $(".player-top").empty();
    $(".player-left").empty();
    $(".player-right").empty();
    $(".player-bottom").empty();
    $("#discard-pile").hide();
};

var sendChatMessage = function (e) {
    var message = $(this).val();

    if (e.which == 13 || e.keyCode == 13) {
        socket.emit("send chat", JSON.stringify({
            username: username,
            message: message
        }));

        $(this).val("");
    }
};

var socketBindings = function () {
    socket.on("connect", function () {
        $("#connecting").hide();
    });

    socket.on("create response", function (id) {
        var link = window.location.origin + "/uno/?join=" + id;

        $("#start-new").show();
        $("#link").show().find("span").html("<b>Give this link to your friend (or enemy): </b>" + "<br/>" + link);
        prepareBoard();
        $("#playarea").show();
        $("#ready-game").on('click', readyGame);
    });

    socket.on("ready game ok", function () {
        $("#link").hide();
    });

    socket.on("game nojoin", function () {
        $("#connecting").text("This game is full or has already started.").show();
    });

    socket.on("receive chat", function (json) {
        var data = JSON.parse(json);

        addChatMessage(data.username, data.message);
    });

    socket.on("receive notification", function (message) {
        addChatNotification(message);
    });

    socket.on("update gamestate", function (json) {
        var data = JSON.parse(json);

        setDiscardCard(data.discard);

        showCardsInHand("player-bottom", data.hand);
        showCardBacksInHand("player-top", data.top);

        if (typeof(data.left) != 'undefined')
            showCardBacksInHand("player-left", data.left);

        if (typeof(data.right) != 'undefined')
            showCardBacksInHand("player-right", data.right);

        if (data.currentplayer == username)
        {
            $(".player-bottom").css("opacity", "1");
            highlightUseableCards();
            hideDrawIfCanPlay();
        }
        else
        {
            $(".player-bottom").css("opacity", "0.5");
        }
    });
};

var addChatNotification = function (message) {
    $(".chat .messages").append('<span class="notification">' + message + '</span>');
    scrollChatToBottom();
};


var addChatMessage = function (c_username, message) {
    var extraClass = "";

    if (c_username == username) {
        extraClass = " me";
    }

    $(".chat .messages").append('<span class="message"><span class="username' + extraClass + '">' + c_username + ': </span>' + message + '</span>');
    scrollChatToBottom();
};

var scrollChatToBottom = function () {
    $(".chat .messages").animate({scrollTop: $('.chat .messages').prop("scrollHeight")}, 350);
};

var showCardsInHand = function (divname, hand) {
    var target = $("." + divname);
    target.empty();

    var html = '<a href="#" style="display: none" class="button-normal" id="draw-card">Draw card</a>';

    for (var i = 0; i < hand.length; i++) {
        var currentCard = hand[i];

        html += '<div class="card small" data-cardcolor="' + currentCard.cardColor.colorName + '" ' +
                'data-cardvalue="' + currentCard.cardValue.valueName + '" ' +
            'style="background-image: url(assets/media/cards/'
            + currentCard.cardColor.colorName + '_' + currentCard.cardValue.valueName + '.png)"></div>';
    }

    target.append(html);
};

var showCardBacksInHand = function (divname, amount) {
    var target = $("." + divname);
    target.empty();

    var html = "";

    for (var i = 0; i < amount; i++) {
        html += '<div class="card small" style="background-image: url(assets/media/cards/back.png)"></div>';
    }

    target.append(html);
};

var setDiscardCard = function (card) {
    var div = $("#discard-pile");
    div.attr("data-cardcolor", card.cardColor.colorName);
    div.attr("data-cardvalue", card.cardValue.valueName);
    div.css("background-image", "url(assets/media/cards/" + card.cardColor.colorName + "_" + card.cardValue.valueName + ".png)");
    div.show();
};

var highlightUseableCards = function () {
    var playerTray = $(".player-bottom");
    var discardCard = $("#discard-pile");
    var match = {
        color: discardCard.attr("data-cardcolor"),
        value: discardCard.attr("data-cardvalue")
    };

    $.each(playerTray.find("div"), function () {
        var target = {
            color: $(this).attr("data-cardcolor"),
            value: $(this).attr("data-cardvalue")
        };

        if (!(target.color == match.color || target.value == match.value || target.color == "special" || match.color == "special"))
        {
            $(this).css("opacity", "0.5");
        }
    });
};

var hideDrawIfCanPlay = function () {
    var canPlay = false;

    var playerTray = $(".player-bottom");
    var discardCard = $("#discard-pile");
    var match = {
        color: discardCard.attr("data-cardcolor"),
        value: discardCard.attr("data-cardvalue")
    };

    $.each(playerTray.find("div"), function () {
        var target = {
            color: $(this).attr("data-cardcolor"),
            value: $(this).attr("data-cardvalue")
        };

        if (target.color == match.color || target.value == match.value || target.color == "special" || match.color == "special")
        {
            canPlay = true;
        }
    });

    if (!canPlay)
    {
        $("#draw-card").show();
    }
};