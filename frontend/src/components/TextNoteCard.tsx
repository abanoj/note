import { Link } from "react-router-dom";
import { Trash2, Pencil, FileText } from "lucide-react";
import type { TextNoteResponseDto } from "../types";

interface Props {
  note: TextNoteResponseDto;
  onDelete: (id: number) => void;
  onEdit: (note: TextNoteResponseDto) => void;
}

export default function TextNoteCard({ note, onDelete, onEdit }: Props) {
  const preview = note.content
    ? note.content.length > 100
      ? note.content.slice(0, 100) + "..."
      : note.content
    : "Sin contenido";

  return (
    <div className="bg-white rounded-lg border border-gray-200 shadow-sm hover:shadow-md transition-shadow">
      <Link to={`/notes/${note.id}`} className="block p-4">
        <div className="flex items-center gap-2 mb-2">
          <FileText className="w-5 h-5 text-amber-500 shrink-0" />
          <h3 className="font-semibold text-gray-900">{note.title}</h3>
        </div>
        <p className="text-sm text-gray-500 line-clamp-2">{preview}</p>
      </Link>

      <div className="border-t border-gray-100 px-4 py-2 flex justify-end gap-1">
        <button
          onClick={() => onEdit(note)}
          className="p-1.5 text-gray-400 hover:text-indigo-600 rounded transition-colors cursor-pointer"
          title="Editar"
        >
          <Pencil className="w-4 h-4" />
        </button>
        <button
          onClick={() => onDelete(note.id)}
          className="p-1.5 text-gray-400 hover:text-red-600 rounded transition-colors cursor-pointer"
          title="Eliminar"
        >
          <Trash2 className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
}
