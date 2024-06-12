# Запуск простой HTML cтраницы на сервере NGINX в Docker-контейнере на AWS инстансе c применением Jenkins CI/CD, Terraform и Ansible

Структура проекта представлена на изображении ниже

:boom: 
## ВНИМАНИЕ, проект предусматривает использование закрытых репозиториев GitHub и  DockerHub. Ниже перечислены необходимы условия для выполнения:
:eight_spoked_asterisk: 1. Учётная запись AMI с соотвествующими правами.
:eight_spoked_asterisk: GitHub репозиторий с доступом по Token
:eight_spoked_asterisk: Jenkins с одним агентом, на агенте установлен Docker, Terraform и Ansible
:eight_spoked_asterisk: Учетная запись в DockerHub и репозиторий для образа
:eight_spoked_asterisk: Зашифрованные данный учётной записи DockerHub посредством Vault

Все Ваши приватные данные заменяются в Jenkinsfile.groovy файле, а так же задаются непосредственно в Jenkins  

   ```tf
   environment {
        AWS_ACCESS_KEY_ID     = credentials('aws-access-key-id')
        AWS_SECRET_ACCESS_KEY = credentials('aws-secret-access-key')
        GIT_TOKEN = credentials('GIT_TOKEN')
        DOCKERHUB_CREDENTIALS = credentials('kirilljbee_dockerhub')
    }
   ```
Пайаплайн запускается с ветки Development, если Вам необходима другая ветка репозитория внесите соотвествующие правки в Jenkinsfile.groovy и настроки пайлайна:

```tf
checkout scmGit(
               branches: [[name: 'development']],
```

Работа производится в ветку development, если предусматривается рабочая ветка с иным именем необходимы внести соотвествующие правки в Jenkinsfile.groovy
Проект предусматривае
> Цель: Ознакомиться с основами инфраструктуры как код и практическим применением Terraform для управления инфраструктурой.

### Содержание:

:heavy_check_mark: 1. Изучите основы инфраструктуры как код и понятия **Terraform**.

:heavy_check_mark: 2. Установите **Terraform** на своей локальной машине согласно инструкциям, предоставленным на официальном сайте **Terraform**.

:heavy_check_mark: 3. Создайте новый проект в **Terraform** и настройте его конфигурацию.

:heavy_check_mark: 4. Определите провайдер, который вы будете использовать (в моём случае это AWS).

:heavy_check_mark: 5. Создайте несколько ресурсов в вашей конфигурации **Terraform** (например, виртуальные машины, сетевые правила, хранилища).

:heavy_check_mark: 6. Определите переменные для вашей конфигурации и используйте их в ресурсах.

:heavy_check_mark: 7. Выполните команду `terraform init` для инициализации проекта и загрузки необходимых провайдеров и модулей.

:heavy_check_mark: 8. Выполните команду `terraform plan` для просмотра плана развертывания и проверки, что ваша конфигурация корректна.

:heavy_check_mark: 9. Выполните команду `terraform apply` для применения изменений и развертывания вашей инфраструктуры.

:heavy_check_mark: 10.Проверьте, что ваша инфраструктура была успешно развернута и функционирует корректно.

:exclamation: 11.(Дополнительное задание) Измените конфигурацию **Terraform**, добавив или изменяя ресурсы, и выполните команды terraform plan и terraform
apply для применения этих изменений.

#### Задание 1. Изучите основы инфраструктуры как код и понятия Terraform.
https://developer.hashicorp.com/terraform/intro

https://developer.hashicorp.com/terraform/tutorials

#### Задание 2. Установите Terraform на своей локальной машине согласно инструкциям, предоставленным на официальном сайте Terraform.
https://developer.hashicorp.com/terraform/install#linux

![terrafom_version](https://github.com/KirillJBee/terraform_project/assets/77605315/ce45e418-08ec-4f95-82be-8fe8e50a0eca)

#### Задания 3 - 6. Создайте новый проект в Terraform и настройте его конфигурацию
Главная задача конфигурации создание инстанса EC2. В конфигурацию были добавлены переменные, а так же создан файл *terraform.tfvars* содержащий значение переменных. Имя файла по-умолчанию позволяет применять переменные автоматически. Файл конфигурации *main.tf* выглядит следующим образом. Вход на AWS через Terraform обеспечен кредами IAM пользователя с сотвествующими правами.
```tf
# Объявление переменных конфигурации
variable "aws_region" {
  description = "Регион AWS"
  type = string
  default = "us-west-1"
}

variable "aws_ami" {
  description = "Образ ami"
  type = string
  default = "ami-0843a4d6dc2130849"
}

variable "name_instance" {
  description = "Название проекта"
  type        = string
  default     = "Default"
}

variable "instance_type" {
  description = "Тип ami"
  type        = string
  default     = "t2.micro"
}

provider "aws" {
  region  = var.aws_region
}


resource "aws_instance" "terraform_instance" {
  ami           = var.aws_ami
  instance_type = var.instance_type

  tags = {
    Name = var.name_instance
  }
}
```
#### Задания 7. Выполните команду `terraform init` 
<img width="800" alt="Screenshot_4" src="https://github.com/KirillJBee/terraform_project/assets/77605315/2edc67b6-c075-4de1-bfc1-1b25068c088c">

#### Задания 8. Выполните команду `terraform plan`
<img width="800" alt="terraform_plan" src="https://github.com/KirillJBee/terraform_project/assets/77605315/2a846500-57e9-447b-9f69-db9dbd169bbb">

#### Задание 9. Выполните команду `terraform apply`
<img width="800" alt="terraform_apply" src="https://github.com/KirillJBee/terraform_project/assets/77605315/aecbb265-e927-4185-a674-da0326898d65">

#### Задание 10. Инфраструктура была успешно развернута и функционирует корректно
Результат выполнения конфигурации **Terraform** в консоли AWS
<img width="800" alt="aws_console" src="https://github.com/KirillJBee/terraform_project/assets/77605315/f0659f06-edf8-4546-8076-29216ffc5405">

#### Задание 11. Измените конфигурацию **Terraform**, добавив или изменяя ресурсы, и выполните команды `terraform plan` и `terraform apply` для применения этих изменений.
В конфигурационный файл был добавлен блок по созданию EBS хранилища и монитирование его к ранее созданому инстансу следующего содержания:
```tf
resource "aws_ebs_volume" "terraform_volume" {
  availability_zone = aws_instance.terraform_instance.availability_zone
  size              = 5
  tags = {
    Name = "terraform_volume"
  }
}

resource "aws_volume_attachment" "volume_attach" {
  device_name = "/dev/sdh"
  volume_id   = aws_ebs_volume.terraform_volume.id
  instance_id = aws_instance.terraform_instance.id
}
```
Результат выполнения команды `terraform plan`:
<img width="800" alt="terraform_plan_2" src="https://github.com/KirillJBee/terraform_project/assets/77605315/003f2cb8-98c5-44d7-bcba-2ed216d2aaab">

Результат выполнения команды `terraform apply`:
<img width="800" alt="terraform_apply_2" src="https://github.com/KirillJBee/terraform_project/assets/77605315/62de4411-d0dd-45c4-89c7-2075d5353c3e">
