# Запуск простой HTML cтраницы на сервере NGINX в Docker-контейнере на AWS инстансе c применением Jenkins CI/CD, Terraform и Ansible

Структура проекта представлена на изображении ниже

![diplomatask (1)](https://github.com/KirillJBee/diplomataskbeta/assets/77605315/65b7780e-b913-4dff-8679-f1551cce3a58)


:boom: 
## ВНИМАНИЕ, проект предусматривает использование закрытых репозиториев GitHub и  DockerHub. Ниже перечислены необходимые условия для выполнения:

:eight_spoked_asterisk: 1. Учётная запись AMI с соотвествующими правами.

:eight_spoked_asterisk: 2. GitHub репозиторий с доступом по Token.

:eight_spoked_asterisk: 3. Для автоматического старта Jenkins при **push** в ветку, активируйте вебхук в настройках GitHub.

:eight_spoked_asterisk: 4. Непосредственно Ваш сайт и его компоненты выгруженные в папку **webpagedata** Вашего репозитория

:eight_spoked_asterisk: 5. Jenkins с одним агентом, на агенте установлен Docker, Terraform и Ansible.

:eight_spoked_asterisk: 6. Учетная запись в DockerHub и репозиторий для образа.

:eight_spoked_asterisk: 7. Зашифрованные данные учётной записи DockerHub посредством Vault.

Вам необходимо описать свои приватные данные в Jenkins Credentials и  указать соотвествующим образом в Jenkinsfile.groovy файле: 

   ```tf
   environment {
        AWS_ACCESS_KEY_ID     = credentials('aws-access-key-id')
        AWS_SECRET_ACCESS_KEY = credentials('aws-secret-access-key')
        GIT_TOKEN = credentials('GIT_TOKEN')
        DOCKERHUB_CREDENTIALS = credentials('kirilljbee_dockerhub')
    }
   ```
Пайаплайн запускается с ветки Development, если Вам необходима другая ветка репозитория внесите соотвествующие правки в Jenkinsfile.groovy и настройки пайплайна:

```tf
checkout scmGit(
               branches: [[name: 'development']],
```
<img width="600" src="https://github.com/KirillJBee/diplomataskbeta/assets/77605315/3153df83-0e7f-46cb-81b1-cdebe3d8493a">

:exclamation: Работа производится в ветку development, если предусматривается ветка с иным именем необходимо внести соотвествующие правки в Jenkinsfile.groovy

:exclamation: Страница сайта будет доступна по адресу http://IP-EC2:5000, где IP-EC2 - это публичный IP адрес созданого инстанса EC2.
Этот адрес можно узнать в Console Output Jenkins после выполнения пайплайна:

<img width="600" src="https://github.com/KirillJBee/diplomataskbeta/assets/77605315/0cebecf0-257d-499f-b4c0-a6b7363319cb">

