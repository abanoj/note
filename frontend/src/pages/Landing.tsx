import { Link } from "react-router-dom";
import { CheckSquare, ListTodo, FileText, BarChart3, Sparkles, Zap, Shield } from "lucide-react";

export default function Landing() {
  return (
    <div className="min-h-screen bg-slate-950 text-white">
      {/* Nav */}
      <nav className="border-b border-white/10">
        <div className="max-w-6xl mx-auto px-4 h-16 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <CheckSquare className="w-6 h-6 text-violet-400" />
            <span className="text-lg font-bold text-gradient">NoteApp</span>
          </div>
          <div className="flex items-center gap-3">
            <Link
              to="/login"
              className="btn-secondary px-4 py-2 rounded-lg text-sm font-medium"
            >
              Iniciar sesion
            </Link>
            <Link
              to="/register"
              className="btn-primary px-4 py-2 rounded-lg text-sm font-medium"
            >
              Comenzar gratis
            </Link>
          </div>
        </div>
      </nav>

      {/* Hero */}
      <section className="max-w-6xl mx-auto px-4 pt-24 pb-20 text-center">
        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-violet-500/10 border border-violet-500/20 text-violet-300 text-sm mb-6">
          <Sparkles className="w-4 h-4" />
          Organiza tu dia de forma simple
        </div>
        <h1 className="text-4xl sm:text-6xl font-extrabold tracking-tight mb-6">
          Tus ideas, tareas y notas
          <br />
          <span className="text-gradient">en un solo lugar</span>
        </h1>
        <p className="text-lg text-slate-400 max-w-2xl mx-auto mb-10">
          Note te ayuda a organizar checklists con seguimiento de progreso
          y notas de texto, todo en una interfaz limpia y moderna.
        </p>
        <div className="flex items-center justify-center gap-4">
          <Link
            to="/register"
            className="btn-primary px-6 py-3 rounded-lg text-base font-semibold"
          >
            Comenzar gratis
          </Link>
          <Link
            to="/login"
            className="btn-secondary px-6 py-3 rounded-lg text-base font-semibold"
          >
            Iniciar sesion
          </Link>
        </div>
      </section>

      {/* Features */}
      <section className="max-w-6xl mx-auto px-4 py-20">
        <h2 className="text-3xl font-bold text-center mb-12">
          Todo lo que necesitas para <span className="text-gradient">mantenerte organizado</span>
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {[
            {
              icon: ListTodo,
              title: "Checklists",
              desc: "Crea listas de tareas con prioridades, estados y seguimiento de progreso en tiempo real.",
            },
            {
              icon: FileText,
              title: "Notas de texto",
              desc: "Escribe y edita notas de texto libre para tus ideas, apuntes y recordatorios.",
            },
            {
              icon: BarChart3,
              title: "Seguimiento de progreso",
              desc: "Visualiza el avance de tus checklists con barras de progreso y estadisticas.",
            },
          ].map(({ icon: Icon, title, desc }) => (
            <div
              key={title}
              className="card p-6 rounded-2xl"
            >
              <div className="w-10 h-10 rounded-lg bg-violet-500/10 flex items-center justify-center mb-4">
                <Icon className="w-5 h-5 text-violet-400" />
              </div>
              <h3 className="text-lg font-semibold text-white mb-2">{title}</h3>
              <p className="text-sm text-slate-400">{desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Pricing */}
      <section className="max-w-6xl mx-auto px-4 py-20">
        <h2 className="text-3xl font-bold text-center mb-12">
          <span className="text-gradient">Planes</span> simples y transparentes
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-3xl mx-auto">
          {/* Free */}
          <div className="card p-8 rounded-2xl border-violet-500/30">
            <div className="flex items-center gap-2 mb-4">
              <Zap className="w-5 h-5 text-violet-400" />
              <h3 className="text-xl font-bold">Free</h3>
            </div>
            <p className="text-3xl font-extrabold mb-1">$0 <span className="text-base font-normal text-slate-400">/mes</span></p>
            <p className="text-sm text-slate-400 mb-6">Perfecto para empezar</p>
            <ul className="space-y-3 text-sm text-slate-300 mb-8">
              <li className="flex items-center gap-2"><CheckSquare className="w-4 h-4 text-violet-400" />Checklists ilimitados</li>
              <li className="flex items-center gap-2"><CheckSquare className="w-4 h-4 text-violet-400" />Notas de texto ilimitadas</li>
              <li className="flex items-center gap-2"><CheckSquare className="w-4 h-4 text-violet-400" />Seguimiento de progreso</li>
            </ul>
            <Link to="/register" className="btn-primary w-full py-2.5 rounded-lg text-sm font-semibold text-center block">
              Comenzar gratis
            </Link>
          </div>

          {/* Pro */}
          <div className="card p-8 rounded-2xl relative overflow-hidden opacity-75">
            <div className="absolute top-4 right-4 px-2 py-0.5 bg-violet-500/20 text-violet-300 text-xs font-medium rounded-full">
              Proximamente
            </div>
            <div className="flex items-center gap-2 mb-4">
              <Shield className="w-5 h-5 text-violet-400" />
              <h3 className="text-xl font-bold">Pro</h3>
            </div>
            <p className="text-3xl font-extrabold mb-1">$5 <span className="text-base font-normal text-slate-400">/mes</span></p>
            <p className="text-sm text-slate-400 mb-6">Para usuarios avanzados</p>
            <ul className="space-y-3 text-sm text-slate-300 mb-8">
              <li className="flex items-center gap-2"><CheckSquare className="w-4 h-4 text-violet-400" />Todo del plan Free</li>
              <li className="flex items-center gap-2"><CheckSquare className="w-4 h-4 text-violet-400" />Etiquetas y categorias</li>
              <li className="flex items-center gap-2"><CheckSquare className="w-4 h-4 text-violet-400" />Exportar a PDF/Markdown</li>
            </ul>
            <button disabled className="w-full py-2.5 rounded-lg text-sm font-semibold bg-slate-700 text-slate-400 cursor-not-allowed">
              Proximamente
            </button>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-white/10 py-8">
        <div className="max-w-6xl mx-auto px-4 flex items-center justify-between text-sm text-slate-500">
          <div className="flex items-center gap-2">
            <CheckSquare className="w-4 h-4 text-violet-400" />
            <span className="font-semibold text-slate-400">NoteApp</span>
          </div>
          <p>&copy; {new Date().getFullYear()} NoteApp by Jesús Abano. Todos los derechos reservados.</p>
        </div>
      </footer>
    </div>
  );
}
