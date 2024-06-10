# Объявление переменных конфигурации
variable "aws_region" {
  description = "Регион AWS"
  type = string
  default = "eu-west-1"
}

provider "aws" {
  region  = var.aws_region
}

// Условия для генерации ключей
resource "tls_private_key" "rsa_4096" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

variable "key_name" {
  description = "Имя приватного ключа"
  type = string
  default = "tf_key.pem"
}

// Создание пары ключей для поключения к инстансу EC2 через SSH
resource "aws_key_pair" "key_pair" {
  key_name   = var.key_name
  public_key = tls_private_key.rsa_4096.public_key_openssh
}

// Сохранение PEM ключа файла локально и изменение прав
resource "local_file" "private_key" {
  content  = tls_private_key.rsa_4096.private_key_pem
  filename = var.key_name

  provisioner "local-exec" {
    command = "chmod 400 ${var.key_name}"
  }
}

# Создание группы безопасности для EC2 
resource "aws_security_group" "sg_terraform" {
  name        = "sg_terraform"
  description = "Security group for terraform EC2"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 5000
    to_port     = 5000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "public_instance" {
  #Образ Canonical, Ubuntu, 22.04 LTS, amd64 jammy image build on 2024-04-11
  ami                    = "ami-0607a9783dd204cae"
  instance_type          = "t2.micro"
  key_name               = aws_key_pair.key_pair.key_name
  vpc_security_group_ids = [aws_security_group.sg_terraform.id]

  tags = {
    Name = "diploma_task"
  }

  provisioner "local-exec" {
    command = "touch dynamic_inventory.ini"
  }

  provisioner "remote-exec" {
    inline = [
      "echo 'EC2 instance is ready.'"
    ]

    connection {
      type        = "ssh"
      host        = self.public_ip
      user        = "ubuntu"
      private_key = tls_private_key.rsa_4096.private_key_pem
    }
  }
}
  
data "template_file" "inventory" {
  template = <<-EOT
    [ec2_instances]
    ${aws_instance.public_instance.public_ip} ansible_user=ubuntu ansible_private_key_file=${path.module}/${var.key_name}
    EOT
}

resource "local_file" "dynamic_inventory" {
  depends_on = [aws_instance.public_instance]

  filename = "dynamic_inventory.ini"
  content  = data.template_file.inventory.rendered

  provisioner "local-exec" {
    command = "chmod 400 ${local_file.dynamic_inventory.filename}"
  }
}

# resource "null_resource" "run_ansible" {
#   depends_on = [local_file.dynamic_inventory]

#   provisioner "local-exec" {
#     command = "ansible-playbook -i dynamic_inventory.ini playbook.yml"
#     working_dir = path.module
#   }
# }
