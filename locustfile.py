"""
Pruebas de Rendimiento con Locust para E-Commerce Microservices
Este archivo simula cargas reales de usuarios en diferentes servicios
"""

from locust import HttpUser, task, between, SequentialTaskSet
import random
import json
from datetime import datetime

class UserBehavior(SequentialTaskSet):
    """
    Comportamiento secuencial de un usuario típico del e-commerce
    """
    
    def on_start(self):
        """Se ejecuta al inicio de cada usuario simulado"""
        self.user_id = None
        self.product_ids = []
        self.order_id = None
        
    @task(1)
    def register_user(self):
        """Tarea 1: Registrar nuevo usuario"""
        user_data = {
            "firstName": f"LoadTest{random.randint(1000, 9999)}",
            "lastName": f"User{random.randint(100, 999)}",
            "email": f"loadtest{random.randint(1000, 9999)}@example.com",
            "phone": f"555{random.randint(1000000, 9999999)}"
        }
        
        with self.client.post("/api/users", 
                             json=user_data,
                             catch_response=True,
                             name="1. Register User") as response:
            if response.status_code == 200:
                self.user_id = response.json().get('userId')
                response.success()
            else:
                response.failure(f"Failed to register user: {response.status_code}")
    
    @task(3)
    def browse_products(self):
        """Tarea 2: Navegar productos (alta frecuencia)"""
        with self.client.get("/api/products",
                            catch_response=True,
                            name="2. Browse All Products") as response:
            if response.status_code == 200:
                products = response.json()
                if products and len(products) > 0:
                    # Guardar algunos product IDs para uso posterior
                    self.product_ids = [p.get('productId') for p in products[:5] if p.get('productId')]
                response.success()
            else:
                response.failure(f"Failed to browse products: {response.status_code}")
    
    @task(2)
    def view_product_details(self):
        """Tarea 3: Ver detalles de producto específico"""
        if not self.product_ids:
            return
            
        product_id = random.choice(self.product_ids)
        with self.client.get(f"/api/products/{product_id}",
                            catch_response=True,
                            name="3. View Product Details") as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Failed to get product details: {response.status_code}")
    
    @task(1)
    def add_to_favourites(self):
        """Tarea 4: Agregar producto a favoritos"""
        if not self.user_id or not self.product_ids:
            return
        
        favourite_data = {
            "userId": self.user_id,
            "productId": random.choice(self.product_ids),
            "likeDate": datetime.now().isoformat()
        }
        
        with self.client.post("/api/favourites",
                             json=favourite_data,
                             catch_response=True,
                             name="4. Add to Favourites") as response:
            if response.status_code in [200, 201, 409]:  # 409 si ya existe
                response.success()
            else:
                response.failure(f"Failed to add favourite: {response.status_code}")
    
    @task(1)
    def create_order(self):
        """Tarea 5: Crear orden de compra"""
        if not self.user_id:
            return
        
        order_data = {
            "orderDate": datetime.now().isoformat(),
            "orderDesc": "Load test order",
            "orderFee": round(random.uniform(50.0, 1000.0), 2),
            "userId": self.user_id
        }
        
        with self.client.post("/api/orders",
                             json=order_data,
                             catch_response=True,
                             name="5. Create Order") as response:
            if response.status_code == 200:
                self.order_id = response.json().get('orderId')
                response.success()
            else:
                response.failure(f"Failed to create order: {response.status_code}")


class ProductServiceLoadTest(HttpUser):
    """
    Escenario de carga específico para Product Service
    Simula usuarios navegando y buscando productos intensivamente
    """
    wait_time = between(1, 3)  # Espera entre 1 y 3 segundos entre tareas
    
    @task(5)
    def get_all_products(self):
        """Lectura masiva de productos"""
        with self.client.get("/api/products",
                            catch_response=True,
                            name="Product: Get All") as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Status: {response.status_code}")
    
    @task(3)
    def get_product_by_id(self):
        """Consulta de producto específico"""
        product_id = random.randint(1, 100)
        with self.client.get(f"/api/products/{product_id}",
                            catch_response=True,
                            name="Product: Get By ID") as response:
            if response.status_code in [200, 404]:  # 404 es esperado si no existe
                response.success()
            else:
                response.failure(f"Status: {response.status_code}")
    
    @task(1)
    def create_product(self):
        """Creación de producto (operación pesada)"""
        product_data = {
            "productTitle": f"LoadTest Product {random.randint(1000, 9999)}",
            "sku": f"LT-{random.randint(10000, 99999)}",
            "priceUnit": round(random.uniform(10.0, 500.0), 2),
            "quantity": random.randint(1, 100),
            "imageUrl": "http://example.com/image.jpg"
        }
        
        with self.client.post("/api/products",
                             json=product_data,
                             catch_response=True,
                             name="Product: Create") as response:
            if response.status_code in [200, 201]:
                response.success()
            else:
                response.failure(f"Status: {response.status_code}")


class OrderServiceLoadTest(HttpUser):
    """
    Escenario de carga específico para Order Service
    Simula creación concurrente de órdenes
    """
    wait_time = between(2, 5)
    
    @task(3)
    def create_concurrent_orders(self):
        """Creación concurrente de órdenes (simula compras simultáneas)"""
        order_data = {
            "orderDate": datetime.now().isoformat(),
            "orderDesc": f"Concurrent Order {random.randint(1, 1000)}",
            "orderFee": round(random.uniform(100.0, 2000.0), 2),
            "userId": random.randint(1, 50)
        }
        
        with self.client.post("/api/orders",
                             json=order_data,
                             catch_response=True,
                             name="Order: Create Concurrent") as response:
            if response.status_code in [200, 201]:
                response.success()
            else:
                response.failure(f"Status: {response.status_code}")
    
    @task(2)
    def get_all_orders(self):
        """Consulta de todas las órdenes"""
        with self.client.get("/api/orders",
                            catch_response=True,
                            name="Order: Get All") as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Status: {response.status_code}")


class PaymentServiceStressTest(HttpUser):
    """
    Prueba de estrés para Payment Service
    Simula múltiples pagos simultáneos
    """
    wait_time = between(1, 2)
    
    @task(4)
    def process_multiple_payments(self):
        """Procesamiento de múltiples pagos (prueba de estrés)"""
        payment_data = {
            "orderId": random.randint(1, 100),
            "isPayed": True,
            "paymentDate": datetime.now().isoformat(),
            "mode": random.choice(["CREDIT_CARD", "DEBIT_CARD", "PAYPAL", "BANK_TRANSFER"]),
            "amount": round(random.uniform(50.0, 5000.0), 2)
        }
        
        with self.client.post("/api/payments",
                             json=payment_data,
                             catch_response=True,
                             name="Payment: Process") as response:
            if response.status_code in [200, 201]:
                response.success()
            else:
                response.failure(f"Status: {response.status_code}")


class ECommerceUser(HttpUser):
    """
    Usuario completo del e-commerce con comportamiento realista
    Combina todas las operaciones en un flujo coherente
    """
    tasks = [UserBehavior]
    wait_time = between(2, 5)  # Tiempo de espera realista entre acciones
    
    # Peso de hosts si tienes múltiples servicios
    host = "http://localhost:8080"  # Ajustar según tu configuración


class FavouriteServiceLoadTest(HttpUser):
    """
    Escenario de carga para Favourite Service
    Prueba operaciones CRUD de favoritos
    """
    wait_time = between(1, 3)
    
    @task(3)
    def get_user_favourites(self):
        """Consultas de favoritos por usuario"""
        user_id = random.randint(1, 50)
        with self.client.get(f"/api/favourites/user/{user_id}",
                            catch_response=True,
                            name="Favourite: Get User Favourites") as response:
            if response.status_code in [200, 404]:
                response.success()
            else:
                response.failure(f"Status: {response.status_code}")
    
    @task(2)
    def add_favourite(self):
        """Agregar favorito"""
        favourite_data = {
            "userId": random.randint(1, 50),
            "productId": random.randint(1, 100),
            "likeDate": datetime.now().isoformat()
        }
        
        with self.client.post("/api/favourites",
                             json=favourite_data,
                             catch_response=True,
                             name="Favourite: Add") as response:
            if response.status_code in [200, 201, 409]:
                response.success()
            else:
                response.failure(f"Status: {response.status_code}")
    
    @task(1)
    def remove_favourite(self):
        """Eliminar favorito"""
        favourite_id = random.randint(1, 100)
        with self.client.delete(f"/api/favourites/{favourite_id}",
                               catch_response=True,
                               name="Favourite: Remove") as response:
            if response.status_code in [200, 204, 404]:
                response.success()
            else:
                response.failure(f"Status: {response.status_code}")


"""
INSTRUCCIONES DE USO:

1. Instalar Locust:
   pip install locust

2. Ejecutar prueba básica:
   locust -f locustfile.py --host=http://localhost:8080

3. Ejecutar con usuarios específicos:
   locust -f locustfile.py --host=http://localhost:8080 --users 100 --spawn-rate 10

4. Ejecutar sin interfaz web (headless):
   locust -f locustfile.py --host=http://localhost:8080 --users 100 --spawn-rate 10 --run-time 5m --headless

5. Ejecutar clase específica:
   locust -f locustfile.py ProductServiceLoadTest --host=http://localhost:8080

6. Generar reporte HTML:
   locust -f locustfile.py --host=http://localhost:8080 --users 50 --spawn-rate 5 --run-time 3m --headless --html report.html

MÉTRICAS A ANALIZAR:
- Requests per second (RPS/Throughput)
- Response time (median, 95th percentile, 99th percentile)
- Failure rate (%)
- Number of users
- Requests made

ESCENARIOS DE PRUEBA RECOMENDADOS:
1. Carga normal: 50 usuarios, spawn-rate 5, 5 minutos
2. Carga alta: 200 usuarios, spawn-rate 10, 10 minutos
3. Estrés: 500 usuarios, spawn-rate 20, 15 minutos
4. Spike test: 1000 usuarios, spawn-rate 100, 5 minutos
"""
