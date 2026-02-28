import { useState, useEffect, type FormEvent } from "react";
import { X } from "lucide-react";
import type { ChecklistResponseDto } from "../types";

interface Props {
  open: boolean;
  editing: ChecklistResponseDto | null;
  onClose: () => void;
  onSubmit: (title: string) => void;
}

export default function CreateChecklistModal({
  open,
  editing,
  onClose,
  onSubmit,
}: Props) {
  const [title, setTitle] = useState("");

  useEffect(() => {
    if (open) {
      setTitle(editing?.title ?? "");
    }
  }, [open, editing]);

  if (!open) return null;

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    if (title.trim()) {
      onSubmit(title.trim());
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black/40 backdrop-blur-sm" onClick={onClose} />
      <div className="relative bg-white rounded-2xl shadow-xl p-6 w-full max-w-sm mx-4">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-base font-semibold text-gray-900">
            {editing ? "Editar checklist" : "Nuevo checklist"}
          </h3>
          <button
            onClick={onClose}
            className="p-1 text-gray-400 hover:text-gray-600 cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <input
            type="text"
            autoFocus
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Nombre del checklist..."
            className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent"
          />
          <div className="mt-4 flex justify-end gap-2">
            <button
              type="button"
              onClick={onClose}
              className="px-3 py-1.5 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors cursor-pointer"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={!title.trim()}
              className="px-3 py-1.5 text-sm font-medium btn-primary rounded-lg disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer"
            >
              {editing ? "Guardar" : "Crear"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
