import { Outlet, Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { LogOut, CheckSquare } from "lucide-react";
import toast from "react-hot-toast";

export default function Layout() {
  const { logoutUser } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logoutUser();
    toast.success("Sesion cerrada");
    navigate("/login");
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b border-gray-200 shadow-sm">
        <div className="max-w-5xl mx-auto px-4 h-14 flex items-center justify-between">
          <Link
            to="/"
            className="flex items-center gap-2 text-gray-900 font-semibold text-lg hover:text-indigo-600 transition-colors"
          >
            <CheckSquare className="w-5 h-5 text-indigo-600" />
            Note
          </Link>
          <button
            onClick={handleLogout}
            className="flex items-center gap-1.5 text-sm text-gray-500 hover:text-red-600 transition-colors cursor-pointer"
          >
            <LogOut className="w-4 h-4" />
            Cerrar sesion
          </button>
        </div>
      </header>
      <main className="max-w-5xl mx-auto px-4 py-8">
        <Outlet />
      </main>
    </div>
  );
}
