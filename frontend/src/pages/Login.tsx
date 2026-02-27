import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { CheckSquare, Loader2 } from "lucide-react";
import toast from "react-hot-toast";
import { AxiosError } from "axios";

export default function Login() {
  const { loginUser } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await loginUser({ email, password });
      toast.success("Bienvenido!");
      navigate("/");
    } catch (err) {
      if (err instanceof AxiosError) {
        toast.error(err.response?.data?.message ?? "Credenciales incorrectas");
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
            Iniciar sesion
          </h2>

          <form onSubmit={handleSubmit} className="space-y-4">
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
              Iniciar sesion
            </button>
          </form>

          <p className="mt-4 text-center text-sm text-gray-500">
            No tienes cuenta?{" "}
            <Link
              to="/register"
              className="text-indigo-600 hover:text-indigo-700 font-medium"
            >
              Registrate
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
