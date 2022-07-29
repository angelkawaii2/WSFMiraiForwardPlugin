# Changelog

> #### v1.4.1 2022-04-03

1. update sdk to v1.4.1

> #### v1.4.0 2022-04-01

1. update sdk to v1.4.0-alpha.2
2. removed ``MiraiWSConnectionManager`` (replaced by ``SDKWebsocketConnManager``)

> #### v1.3.0 2022-02-21

1. update sdk to v1.4.0-alpha.1

> #### v1.2.1 2022-02-19

1. update sdk to v1.3.1(fix timeout while shutdown issue)

> #### v1.2.0 2022-02-06

1. update java to 17
2. update mirai-console to 2.10.0

> #### v1.1.1 2021-08-19

1. fix displayServerName display wrongly as worldName if not rewrite.
2. update mirai dependency 2.6.4 to 2.6.7

> #### v1.1.0 2021-06-25
> 1. rewrite ``MiraiWsListener`` extends from `AbstractSDKWsListener`
> 2. deprecate old ``MiraiWsListener``
> 3. add handler class for ``MiraiWsListener``
> 4. update ``MiraiWSConnectionManager`` to new ``MiraiWsListener``
> 5. add ``minecrat-chat-servername-rewrite`` config
> #### v1.0.1 2021-05-31
> 1. optimize output message in websocket OnMsg event
> #### v1.0.0
> 1. release
