# Jenkins Pipelines - E-commerce Microservices

Este directorio contiene los archivos Jenkinsfile para los pipelines CI/CD de cada microservicio.

## 📋 Archivos Disponibles

### Ambiente DEV
- `Jenkinsfile-user-service-dev` - Pipeline para user-service
- `Jenkinsfile-product-service-dev` - Pipeline para product-service
- `Jenkinsfile-order-service-dev` - Pipeline para order-service
- `Jenkinsfile-payment-service-dev` - Pipeline para payment-service
- `Jenkinsfile-shipping-service-dev` - Pipeline para shipping-service
- `Jenkinsfile-favourite-service-dev` - Pipeline para favourite-service

## 🚀 Cómo Crear los Jobs en Jenkins

Para cada microservicio, sigue estos pasos:

### 1. Crear Nuevo Pipeline

1. Abre Jenkins: `http://localhost:8081`
2. Click en **"New Item"**
3. Nombre del job: `user-service-dev` (cambia según el servicio)
4. Selecciona **"Pipeline"**
5. Click **"OK"**

### 2. Configurar el Pipeline

En la página de configuración del job:

#### General
- ✅ Marca **"This project is parameterized"** (opcional, si quieres parámetros)
- Descripción: `Pipeline DEV para [nombre-servicio]`

#### Build Triggers (opcional)
- ✅ **"GitHub hook trigger for GITScm polling"** - Para auto-build cuando hay commits
- ✅ **"Poll SCM"** - Schedule: `H/5 * * * *` (cada 5 minutos)

#### Pipeline
- **Definition:** Pipeline script from SCM
- **SCM:** Git
- **Repository URL:** `https://github.com/SelimHorri/ecommerce-microservice-backend-app.git`
- **Branch:** `*/master`
- **Script Path:** `jenkins/Jenkinsfile-user-service-dev` (cambia según el servicio)

#### Configuración Avanzada (opcional)
- **Lightweight checkout:** ✅ Marcado

### 3. Guardar y Ejecutar

1. Click **"Save"**
2. Click **"Build Now"**
3. Ve a **"Console Output"** para ver el progreso

## 📝 Jobs a Crear

Crea estos 6 jobs siguiendo los pasos anteriores:

| Job Name | Script Path |
|----------|-------------|
| `user-service-dev` | `jenkins/Jenkinsfile-user-service-dev` |
| `product-service-dev` | `jenkins/Jenkinsfile-product-service-dev` |
| `order-service-dev` | `jenkins/Jenkinsfile-order-service-dev` |
| `payment-service-dev` | `jenkins/Jenkinsfile-payment-service-dev` |
| `shipping-service-dev` | `jenkins/Jenkinsfile-shipping-service-dev` |
| `favourite-service-dev` | `jenkins/Jenkinsfile-favourite-service-dev` |

## ⚙️ Variables de Entorno

Cada Jenkinsfile usa estas variables:

- `SERVICE_NAME` - Nombre del microservicio
- `DOCKER_IMAGE` - `tyronemab/[service-name]`
- `DOCKER_TAG` - `dev-${BUILD_NUMBER}`
- `MAVEN_OPTS` - Opciones de Maven

## 🔧 Etapas del Pipeline

1. **Checkout** - Clona el repositorio desde GitHub
2. **Build** - Compila el servicio con Maven
3. **Docker Build** - Construye la imagen Docker
4. **Docker Push** - Publica la imagen a Docker Hub
5. **Deploy to Dev** - Despliega en Kubernetes namespace `dev`

## ✅ Prerequisitos

Antes de ejecutar los pipelines, asegúrate de tener:

- ✅ Credenciales `dockerhub-credentials` configuradas en Jenkins
- ✅ Kubectl configurado en el agente de Jenkins
- ✅ Kubernetes cluster corriendo (Minikube)
- ✅ Namespace `dev` creado en Kubernetes
- ✅ Deployments de Kubernetes creados (ver directorio `/k8s/dev/`)

## 🐛 Troubleshooting

### Error: "dockerhub-credentials not found"
```bash
# Crear credenciales en Jenkins
Manage Jenkins → Credentials → System → Global → Add Credentials
- Kind: Username with password
- ID: dockerhub-credentials
- Username: tyronemab
- Password: [tu password de Docker Hub]
```

### Error: "kubectl command not found"
```bash
# Instalar kubectl en el contenedor Jenkins
docker exec -it -u root jenkins bash
apt-get update && apt-get install -y kubectl
```

### Error: "Cannot connect to Docker daemon"
```bash
# Dar permisos al usuario jenkins para usar Docker
docker exec -it -u root jenkins bash
usermod -aG docker jenkins
# Reiniciar Jenkins
docker restart jenkins
```

## 📊 Monitoreo

Después de ejecutar los pipelines:

1. Verifica el estado en Jenkins: `http://localhost:8081`
2. Verifica las imágenes en Docker Hub: `https://hub.docker.com/r/tyronemab`
3. Verifica los pods en Kubernetes:
   ```bash
   kubectl get pods -n dev
   kubectl get deployments -n dev
   ```

## 🔄 Próximos Pasos

Una vez que los pipelines DEV estén funcionando:

1. Crear pipelines para STAGE (`Jenkinsfile-stage`)
2. Crear pipelines para PROD (`Jenkinsfile-prod`)
3. Implementar pruebas en los pipelines
4. Configurar notificaciones
5. Agregar análisis de código estático

---

**Nota:** Estos pipelines están configurados para el usuario de Docker Hub `tyronemab`. Si usas otro usuario, actualiza la variable `DOCKER_IMAGE` en cada Jenkinsfile.
