# fisa240220_2
 
 
## 목차

#### 1\. [Intro](https://yerinpark.tistory.com/143#link1)

#### 2\. [AWS EC2 인스턴스 생성](https://yerinpark.tistory.com/143#link2)

#### 3\. [AWS EC2에 설치 및 환경 설정](https://yerinpark.tistory.com/143#link3)

#### 4\. [Jenkins 설정하기](https://yerinpark.tistory.com/143#link4)

#### 5\. [Jenkins 빌드 시 다운되는 현상](https://yerinpark.tistory.com/143#link5)

#### 6\. [결과](https://yerinpark.tistory.com/143#link6)

## Intro

어제까지 VirtualBox의 Linux 환경에서 Docker를 사용하여 Jenkins를 설치했다. 오늘은 AWS EC2에서 Docker를 사용하여 Jenkins를 설치해본 후, GitHub Webhook을 설정하여 이벤트가 발생하면 자동으로 빌드가 되게 했다.

## AWS EC2 인스턴스 생성

AWS I AM 계정을 받은 후 EC2 서비스에서 키 페어를 생성해줬다.

**서비스 > EC2 > 키 페어 생성**에서 해준다.

키 페어는 탈취 당하면 안 되기 때문에 온라인 상에 올리지 않도록 주의한다.


![image](https://github.com/parfait2/fisa240220_2/assets/60564216/bfffa4b3-4f10-4e28-b17a-10d6619545ec)
![image](https://github.com/parfait2/fisa240220_2/assets/60564216/5690a467-e5de-48b2-b526-85c268e954b0)


이제 EC2 인스턴스를 시작해보자.

**EC2 대시보드 > 인스턴스 시작 버튼**을 클릭한다.
![image](https://github.com/parfait2/fisa240220_2/assets/60564216/9a0beb03-6315-40b2-9164-8b2062ace691)

필요한 소프트웨어 구성은 **Ubuntu**로 해줬다.

![image](https://github.com/parfait2/fisa240220_2/assets/60564216/6bc865ee-6e31-403d-9118-c1cb25ef70e1)


추후 권한 설정 관련 편의를 위해 보안 그룹 생성 시 규칙을 모두 선택하고, 위치 무관한 곳에서도 접근 가능하도록 설정했다. 보안에 민감한 부분일 수 있으니 옵션을 사용자에 맞게 선택한다.

## AWS EC2에 설치 및 환경 설정

인스턴스 시작을 한 후 AWS EC2 인스턴스를 MobaXterm에서 세션으로 연다.

![image](https://github.com/parfait2/fisa240220_2/assets/60564216/ceadacbc-27a9-4fc5-bbcd-831511317768)

> Session settings > SSH

Basic SSH setting에서 **Remote host**에 인스턴스의 **public ip**를 입력한다.

인스턴스의 public ip는 AWS 대시보드에서

> EC2 > 인스턴스 > \[인스턴스 ID\]

퍼블릭 IPv4 주소에서 확인할 수 있다.

Advanced SSH settings에서 **Use private key** 체크 박스를 선택하고 파란색 파일 아이콘을 눌러 아까 로컬에 다운로드 받았던 키 페어 파일을 선택한 후 OK 버튼을 누른다.

![image](https://github.com/parfait2/fisa240220_2/assets/60564216/4062253b-5098-4a9e-ad97-da24e93bf3b7)

MobaXterm의 세션으로 연결한 화면이다.

login as : **ubuntu**

를 입력하면 된다.

```
cat /etc/os-release

sudo apt update

sudo apt install openjdk-17-jre-headless

sudo snap install docker

sudo docker run --name myjenkins --privileged -d -p 8080:8080 jenkins/jenkins:lts-jdk17
```

> cat /etc/os-release

명령어로 현재 시스템의 운영 체제 정보를 확인해보았다.

> sudo apt update

명령어로 시스템 패키지 목록을 최신으로 업데이트했다.

> sudo apt install openjdk-17-jre-headless

명령어로 OpenJDK 17 버전의 JRE(Java Runtime Environment)를 설치했다.

Jenkins는 Java 기반 어플리케이션이므로 Java 실행 환경이 필요하다.

> sudo snap install docker

명령어로 docker를 설치했다.

> sudo docker run --name myjenkins --privileged -d -p 8080:8080 jenkins/jenkins:lts-jdk17

명령어로 docker를 사용하여 Jenkins 컨테이너를 실행했다.

컨테이너 이름은 myjenkins로 설정하고, 호스트의 8080 포트와 컨테이너의 8080 포트를 매핑해주었다.

이렇게 해서 Docker 컨테이너로 Jenkins를 실행하고, 웹 브라우저를 통해 http://{사용자의 AWS EC2 인스턴스 ip 주소}:8080으로 접속하여 Jenkins를 설정할 수 있다.

참고) 현재 실행 중인 컨테이너를 확인해보면 다음과 같이 나온다.

```
ubuntu@ip-172-31-34-137:~$ sudo docker ps -a
CONTAINER ID   IMAGE                       COMMAND                  CREATED          STATUS          PORTS                                                  NAMES
e24e45937993   jenkins/jenkins:lts-jdk17   "/usr/bin/tini -- /u…"   20 seconds ago   Up 15 seconds   0.0.0.0:8080->8080/tcp, :::8080->8080/tcp, 50000/tcp   myjenkins
```

### Jenkins 설정하기

Jenkins를 웹 브라우저에서 열기 위해서는 AWS EC2에서 **인바운드 규칙 편집**을 해줘야 한다.

![image](https://github.com/parfait2/fisa240220_2/assets/60564216/c1b26918-46a8-40dd-9974-44f1d0108105)

> EC2 > 보안 그룹 > {AWS EC2 인스턴스 ID} > 인바운드 규칙 편집

에서 **규칙 추가 버튼**을 누른다.

유형 : 사용자 지정 TCP

포트 범위 : 8080

소스 : 0.0.0.0/0

입력 후 **규칙 저장**  버튼을 누른다.

![image](https://github.com/parfait2/fisa240220_2/assets/60564216/44396e65-2ada-4ecc-aabd-325ea4761418)

> {AWS EC2 instance public ip}:8080

으로 접속해보면 웹 브라우저가 열린다.

만일 Jenkins의 Administrator password를 확인하고 싶다면

> sudo docker exec myjenkins sh -c 'cat /var/jenkins\_home/secrets/initialAdminPassword' 

이 명령어로 찾을 수 있다.
![image](https://github.com/parfait2/fisa240220_2/assets/60564216/ea45f4ed-93d7-48ad-b687-be1310510ccf)

Instance Configuration의 Jenkins URL을

> {AWS EC2 인스턴스의 public ip 주소}:8080/

라고 입력한다. 포트는 **sudo docker run --name myjenkins --privileged -d -p 8080:8080 jenkins/jenkins:lts-jdk17**

여기서 매핑한 포트이다.

GitHub에서 **Webhook**을 설정해서 push할 때마다 Jenkins에서 자동으로 빌드되도록 해보자.

여기서 Webhook이란 이벤트 기반으로 작동하는 HTTP 콜백이다. 여러 서비스 간에 실시간으로 데이터를 전달하거나 특정 이벤트가 발생할 때 자동으로 작업을 수행하기 위해 사용된다.

Jenkins에서 GitHub과의 Webhook을 설정하면 GitHub 저장소에 변경 사항이 발생할 때마다 Jenkins는 Webhook을 통해 해당 이벤트를 감지하고 자동으로 빌드 및 배포 작업을 수행할 수 있다. Webhook을 설정하면 소스 코드 변경 및 이벤트에 빠르게 대응할 수 있으며, 개발 워크플로우를 자동화하여 개발 및 배포 프로세스를 효율적으로 관리할 수 있다.

![image](https://github.com/parfait2/fisa240220_2/assets/60564216/16517a00-b9fa-486e-ba02-01abf326964e)

GitHub의 Repository > Settings > Webhooks > Payload URL에

> {AWS EC2 인스턴스의 public ip}:{포트}/github-webhook/

라고 입력해준다. (\*webhook 뒤에 꼭 슬래시를 써줘야 한다.)

다시 Jenkins로 돌아와서
![image](https://github.com/parfait2/fisa240220_2/assets/60564216/58bc192c-8912-4534-bf25-e62ab70852e2)

> Dashboard > Jenkins 관리 > Credentials > System > Global credentails

Kind : **Username with password 선택**

Username : **GitHub id**

password : **GitHub password**

![image](https://github.com/parfait2/fisa240220_2/assets/60564216/3fd9a78f-2d4b-4016-803b-556264a95669)

> Dashboard > Jenkins 관리 > Tools

Gradle을 다음과 같이 설정해준다.

![image](https://github.com/parfait2/fisa240220_2/assets/60564216/7c1a1854-be13-4390-9f63-7959170ceab7)

> Dashboard > {jenkins item id} > Pipleline Syntax에서

Sample Step : **git: Git 선택**

Repository URL : **나의 GitHub Repository url**

Branch : **main**

Credentials : **위에서 설정해준 Credentials 선택**

체크 박스 둘 다 선택 후 Generate Pipeline Script 클릭

으로 설정해준다. GitHub의 브랜치는 사용자에 맞게 쓰면 된다.

클릭해서 나온 밑의 결과를 복사해둔다.

![image](https://github.com/parfait2/fisa240220_2/assets/60564216/d6725b6c-ab5a-48b7-8712-80f7707b2a1a)

> Dashboard > {jenkins item id} > Configuration

Pipeline script를 다음과 같이 적어주었다.

```
pipeline {
    agent any

    stages {
        stage('git clone') {
            steps {
                echo 'clone start'
                git branch: 'main', credentialsId: '--------', url: 'https://github.com/parfait2/fisa240220_2.git'
                echo 'clone end'
            }
        }
        
        stage('list view') {
            steps {
                echo '리스트 보기'
                sh ''' ls '''
            }
        }
        
        stage('Build') {
            steps {
                echo 'Building Gradle project'
                sh ''' ./gradlew build '''
            }
        }
    }
}
```

여기서, Jenkins 빌드 시 다운되는 현상이 발생할 수 있다.

### Jenkins 빌드 시 다운되는 현상

![image](https://github.com/parfait2/fisa240220_2/assets/60564216/d4b6dce3-ce7a-4d96-9e65-d820e3324a68)

AWS EC2는 보통 프리티어 사용 가능한 **micro.t2**를 많이 사용한다. 1GB 남짓한 용량으로 자주 다운되는 현상을 겪을 수 있다.

이런 경우에는 Ubuntu 서버에서 **Swap 파일**을 통해 해결할 수 있다. Swap은 시스템이 메모리 부족 상태에서 추가 메모리를 제공하며, 서버의 안정성과 성능을 향상시킬 수 있다.

```
free -h

sudo dd if=/dev/zero of=/swapfile bs=128M count=16

sudo chmod 600 /swapfile 

sudo mkswap /swapfile 

sudo swapon /swapfile 

sudo swapon -s 

sudo vi /etc/fstab 
/* vi 편집기로 열어서 밑의 내용을 맨 밑에 추가한다. */
/swapfile swap swap defaults 0 0

free -h 명령어
```

1.  **free -h**
    -   현재 메모리 및 스왑 상태를 표시한다. 여기서 \*\*Mem\*\*은 물리적인 메모리(RAM)를 나타내며, \*\*Swap\*\*은 스왑 파일 또는 스왑 파티션의 크기를 나타낸다.
2.  **sudo dd if=/dev/zero of=/swapfile bs=128M count=16**
    -   2GB 크기의 스왑 파일을 생성한다. \*\*/swapfile\*\*은 스왑 파일의 경로이며, \*\*bs=128M\*\*은 블록 크기를 나타냅니다.
3.  **sudo chmod 600 /swapfile** 
    -   스왑 파일에 대한 보안을 강화하기 위해 파일의 권한을 변경한다. 이 명령어는 오너에게 읽기와 쓰기 권한만 부여한다.
4.  **sudo mkswap /swapfile** 
    -   스왑 파일을 초기화하고 스왑 시스템을 설정한다.
5.  **sudo swapon /swapfile** 
    -   스왑 파일을 활성화한다.
6.  **sudo swapon -s** 
    -   현재 활성화된 스왑의 상태를 표시한다. 여기서는 \*\*/swapfile\*\*이라는 스왑 파일이 활성화되어 있음을 보여준다.
7.  **sudo vi /etc/fstab** 
    -   **vi** 편집기를 사용하여 **/etc/fstab** 파일을 열어 스왑 파일이 부팅 시 자동으로 활성화되도록 설정한다. 파일의 맨 아래에
    -   **/swapfile swap swap defaults 0 0**
    -   라는 내용을 추가한다.
8.  **free -h**
    -   최종적으로 스왑이 제대로 설정되었는지 확인한다. \*\*Swap\*\*의 값이 변경되어 있어야 한다.

전체 코드는 다음과 같다.

```
ubuntu@ip-172-31-34-137:~$ free -h
               total        used        free      shared  buff/cache   available
Mem:           949Mi       216Mi       121Mi       0.0Ki       611Mi       575Mi
Swap:             0B          0B          0B
ubuntu@ip-172-31-34-137:~$ sudo dd if=/dev/zero of=/swapfile bs=128M count=16
16+0 records in
16+0 records out
2147483648 bytes (2.1 GB, 2.0 GiB) copied, 31.2297 s, 68.8 MB/s
ubuntu@ip-172-31-34-137:~$ sudo chmod 600 /swapfile
ubuntu@ip-172-31-34-137:~$ sudo mkswap /swapfile
Setting up swapspace version 1, size = 2 GiB (2147479552 bytes)
no label, UUID=0a197d08-6ceb-46b0-beb5-786c9e11f3d6
ubuntu@ip-172-31-34-137:~$ sudo swapon /swapfile
ubuntu@ip-172-31-34-137:~$ sudo swapon -s
Filename                                Type            Size            Used            Priority
/swapfile                               file            2097148         0               -2
ubuntu@ip-172-31-34-137:~$ sudo vi /etc/fstab
/* vi 편집기에서 맨 밑 줄에 다음 내용을 추가한다. */
/swapfile swap swap defaults 0 0
ubuntu@ip-172-31-34-137:~$ sudo vi /etc/fstab
ubuntu@ip-172-31-34-137:~$ free -h
               total        used        free      shared  buff/cache   available
Mem:           949Mi       213Mi       144Mi       0.0Ki       591Mi       573Mi
Swap:          2.0Gi          0B       2.0Gi
ubuntu@ip-172-31-34-137:~$
```

이렇게 swap을 추가한 후 AWS EC2 인스턴스 중지 후 다시 시작해주면 된다.

다시 시작한 인스턴스의 public ip는 변경되어 있으니 유념하자.

```
ubuntu@ip-172-31-34-137:~$ sudo docker ps -a
CONTAINER ID   IMAGE                       COMMAND                  CREATED       STATUS                       PORTS                                                  NAMES
e24e45937993   jenkins/jenkins:lts-jdk17   "/usr/bin/tini -- /u…"   2 hours ago   Exited (255) 5 minutes ago   0.0.0.0:8080->8080/tcp, :::8080->8080/tcp, 50000/tcp   myjenkins
ubuntu@ip-172-31-34-137:~$ sudo docker start myjenkins
myjenkins
ubuntu@ip-172-31-34-137:~$ sudo docker ps -a
CONTAINER ID   IMAGE                       COMMAND                  CREATED       STATUS         PORTS                                                  NAMES
e24e45937993   jenkins/jenkins:lts-jdk17   "/usr/bin/tini -- /u…"   2 hours ago   Up 5 seconds   0.0.0.0:8080->8080/tcp, :::8080->8080/tcp, 50000/tcp   myjenkins
ubuntu@ip-172-31-34-137:~$
```

도커도 꺼져 있어서 docker start 명령어로 다시 켜줬다.

### 결과

![image](https://github.com/parfait2/fisa240220_2/assets/60564216/a2701ad3-418a-4a7b-b0bb-259f0e1159b0)

#7이 swap 설정 전이고, #8이 swap 설정 후이다. 추가 메모리 설정 후에는 무한 로딩되지 않고 정상적으로 빌드된 것을 확인할 수 있다.
