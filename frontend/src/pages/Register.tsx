import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { CheckSquare, Loader2 } from "lucide-react";
import toast from "react-hot-toast";
import { AxiosError } from "axios";

export default function Register() {
  const { registerUser } = useAuth();
  const navigate = useNavigate();
  const [firstname, setFirstname] = useState("");
  const [lastname, setLastname] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await registerUser({ firstname, lastname, email, password });
      toast.success("Cuenta creada!");
      navigate("/");
    } catch (err) {
      if (err instanceof AxiosError) {
        const data = err.response?.data;
        if (data?.messages) {
          data.messages.forEach((msg: string) => toast.error(msg));
        } else {
          toast.error(data?.message ?? "Error al registrar");
        }
      } else {
        toast.error("Error de conexion");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
      <div className="w-full max-w-sm">
        <div className="flex items-center justify-center gap-2 mb-8">
          <CheckSquare className="w-8 h-8 text-indigo-600" />
          <h1 className="text-2xl font-bold text-gray-900">TaskList</h1>
        </div>

        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">
            Crear cuenta
          </h2>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Nombre
                </label>
                <input
                  type="text"
                  required
                  value={firstname}
                  onChange={(e) => setFirstname(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Apellido
                </label>
                <input
                  type="text"
                  required
                  value={lastname}
                  onChange={(e) => setLastname(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Email
              </label>
              <input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                placeholder="tu@email.com"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Contrasena
              </label>
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                placeholder="********"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-indigo-600 text-white py-2 px-4 rounded-md text-sm font-medium hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex items-center justify-center gap-2 cursor-pointer"
            >
              {loading && <Loader2 className="w-4 h-4 animate-spin" />}
              Registrarse
            </button>
          </form>

          <p className="mt-4 text-center text-sm text-gray-500">
            Ya tienes cuenta?{" "}
            <Link
              to="/login"
              className="text-indigo-600 hover:text-indigo-700 font-medium"
            >
              Inicia sesion
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
