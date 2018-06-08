  function decode(protocol, host, port) {
    var url = protocol + "://" + host + ":" + port + "/oauth/token/decode";

    var jsonData = {};

    $.ajax(url, {
      type: "POST",
      contentType: 'application/json',
      data: $( "#access_token" ).val(),
      success: function (data) {
            $('#decoded_access_token').val(JSON.stringify(data.access_token, null, 4));
            $('#decoded_id_token').val(JSON.stringify(data.id_token, null, 4));
      }
    });
  }