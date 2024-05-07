- 로컬 개발 환경 리버스 프록시
  - ex) jsp 개발자가 API 연동 시 로컬에서 스프링부트를 구동해서 작업해야 하는 번거로움 감소
  - ex) 페이지에 리액트, JSP 가 섞여있는 개발환경에서 리액트-JSP 간의 전환을 확인하기 위해 매번 개발서버로 배포하여 확인하는 수고로움 감소
- MacOS zsh 환경에서 구동 스크립트
- 컨테이너 내부에서 호스트 프로세스에 접근하기 위한 네트워크 설정 진행
  - ex) `--network="host"`, keyword `host.docker.internal`
  - [orbstack](https://orbstack.dev/) 사용 시 바로 사용가능
  - [docker-desktop](https://www.docker.com/products/docker-desktop/) 사용 시 전역설정 필요 
- 사용법
  - uri/local_uris, uri/remote_uris 파일에 각각 라우팅할 uri 목록 추가   
  - 구동
    ```sh
    $ ./startup.sh ${환경} ${포트}
    $ ./startup.sh dev 8080
    ```
  - 종료
    ```sh
    ./shutdown.sh
    ```
    