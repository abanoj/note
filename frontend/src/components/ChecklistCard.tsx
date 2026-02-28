import { Link } from "react-router-dom";
import { Trash2, Pencil, ListTodo } from "lucide-react";
import type { ChecklistResponseDto } from "../types";

interface Props {
  checklist: ChecklistResponseDto;
  onDelete: (id: number) => void;
  onEdit: (checklist: ChecklistResponseDto) => void;
}

export default function ChecklistCard({ checklist, onDelete, onEdit }: Props) {
  const progress = checklist.progress ?? 0;
  const progressPercent = Math.round(progress * 100);

  return (
    <div className="bg-white rounded-xl border border-gray-200 shadow-sm hover:shadow-md transition-all hover:-translate-y-0.5">
      <Link to={`/lists/${checklist.id}`} className="block p-4">
        <div className="flex items-start justify-between">
          <div className="flex items-center gap-2">
            <ListTodo className="w-5 h-5 text-violet-500 shrink-0" />
            <h3 className="font-semibold text-gray-900">{checklist.title}</h3>
          </div>
          <span className="text-xs text-gray-400 shrink-0 ml-2">
            {checklist.numberOfItems}{" "}
            {checklist.numberOfItems === 1 ? "item" : "items"}
          </span>
        </div>

        {checklist.numberOfItems > 0 && (
          <div className="mt-3">
            <div className="flex items-center justify-between text-xs text-gray-500 mb-1">
              <span>Progreso</span>
              <span>{progressPercent}%</span>
            </div>
            <div className="w-full bg-gray-100 rounded-full h-1.5">
              <div
                className="h-1.5 rounded-full bg-gradient-to-r from-violet-500 to-indigo-500 transition-all duration-300"
                style={{ width: `${progressPercent}%` }}
              />
            </div>
          </div>
        )}
      </Link>

      <div className="border-t border-gray-100 px-4 py-2 flex justify-end gap-1">
        <button
          onClick={() => onEdit(checklist)}
          className="p-1.5 text-gray-400 hover:text-violet-600 rounded transition-colors cursor-pointer"
          title="Editar"
        >
          <Pencil className="w-4 h-4" />
        </button>
        <button
          onClick={() => onDelete(checklist.id)}
          className="p-1.5 text-gray-400 hover:text-red-600 rounded transition-colors cursor-pointer"
          title="Eliminar"
        >
          <Trash2 className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
}
