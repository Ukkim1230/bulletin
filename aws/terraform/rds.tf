# RDS 서브넷 그룹
resource "aws_db_subnet_group" "main" {
  name       = "${var.project_name}-db-subnet-group"
  subnet_ids = aws_subnet.private[*].id

  tags = {
    Name = "${var.project_name}-db-subnet-group"
  }
}

# RDS 파라미터 그룹
resource "aws_db_parameter_group" "main" {
  family = "mysql8.0"
  name   = "${var.project_name}-db-params"

  parameter {
    name  = "character_set_server"
    value = "utf8mb4"
  }

  parameter {
    name  = "collation_server"
    value = "utf8mb4_unicode_ci"
  }

  tags = {
    Name = "${var.project_name}-db-parameter-group"
  }
}

# RDS 인스턴스
resource "aws_db_instance" "main" {
  identifier = "${var.project_name}-db"

  # 엔진 설정
  engine         = "mysql"
  engine_version = "8.0"
  instance_class = "db.t3.micro"

  # 스토리지 설정
  allocated_storage     = 20
  max_allocated_storage = 100
  storage_type          = "gp2"
  storage_encrypted     = true

  # 데이터베이스 설정
  db_name  = "church_bulletin"
  username = var.db_username
  password = var.db_password
  port     = 3306

  # 네트워크 설정
  vpc_security_group_ids = [aws_security_group.rds.id]
  db_subnet_group_name   = aws_db_subnet_group.main.name
  parameter_group_name   = aws_db_parameter_group.main.name

  # 백업 설정
  backup_retention_period = 7
  backup_window          = "03:00-04:00"
  maintenance_window     = "sun:04:00-sun:05:00"

  # 기타 설정
  skip_final_snapshot       = true
  deletion_protection       = false
  auto_minor_version_upgrade = true
  multi_az                  = false

  tags = {
    Name = "${var.project_name}-database"
  }
}

# SSM Parameters for database connection
resource "aws_ssm_parameter" "db_username" {
  name  = "/${var.project_name}/db/username"
  type  = "String"
  value = aws_db_instance.main.username

  tags = {
    Name = "${var.project_name}-db-username"
  }
}

resource "aws_ssm_parameter" "db_password" {
  name  = "/${var.project_name}/db/password"
  type  = "SecureString"
  value = var.db_password

  tags = {
    Name = "${var.project_name}-db-password"
  }
}

resource "aws_ssm_parameter" "db_url" {
  name  = "/${var.project_name}/db/url"
  type  = "String"
  value = "jdbc:mysql://${aws_db_instance.main.endpoint}/${aws_db_instance.main.db_name}?useSSL=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8"

  tags = {
    Name = "${var.project_name}-db-url"
  }
}
