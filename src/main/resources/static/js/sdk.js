

(function (win) {
    var _queryParameters = {
        'response_type': 'code'
    };
    function readMeta(name) {
        var metaElement = document.querySelector('meta[name=verimi-signin-' + name + ']'),
            metaValue = null;
        if (metaElement) {
            metaValue = metaElement.getAttribute('content');
        }
        return metaValue;
    }
    function readRequiredMeta(name) {
        var metaValue = readMeta(name);
        if (!metaValue) {
            throw new Error('No ' + name + ' was set.');
        }
        return metaValue;
    }
    function init() {
        _client_id = readRequiredMeta('client_id');
        _queryParameters['redirect_uri'] = readMeta('redirect_uri');
        _queryParameters['scope'] = readMeta('scope');
        _queryParameters['state'] = readMeta('state');
        bindStartToButton();
    }
    function startLogin(scope, state) {
        var protocol = document.location.protocol;
        var domain = document.location.host;
        if (scope) {
            _queryParameters['scope'] = scope;
        }
        if (state) {
            _queryParameters['state'] = state;
        }
        var queryParameterString = Object.keys(_queryParameters)
            .filter(function (qpKey) {
                return _queryParameters[qpKey] !== null;
            })
            .map(function (qpKey) {
                return qpKey + '=' + _queryParameters[qpKey];
            }).join('&');
        var URL = [
            readRequiredMeta('authorization_server_protocol'),
            '://',
            readRequiredMeta('authorization_server_host'),
            ':',
            readRequiredMeta('authorization_server_port'),
            '/oauth/service_provider_access/',
            _client_id,
            '?',
            queryParameterString
        ].join('');

        document.location.href = URL;
    }
    function bindStartToButton() {
        var foundButtons = document.getElementsByClassName('verimi-signin-login-button');
        if (foundButtons.length > 0) {
            var buttonObject = foundButtons.item(0);
            buttonObject.addEventListener('click', function () {
                var scope = buttonObject.getAttribute('data-scope'),
                    state = buttonObject.getAttribute('data-state');
                startLogin(scope, state);
            });
        }
    }
    win.Verimi = {
        init: init
    };
})(window);
window.Verimi.init();
