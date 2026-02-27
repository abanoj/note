import { useState, useRef, useEffect } from "react";
import { Trash2, GripVertical } from "lucide-react";
import type { ItemDto, ItemStatus, ItemPriority } from "../types";

interface Props {
  item: ItemDto;
  onStatusChange: (item: ItemDto, status: ItemStatus) => void;
  onPriorityChange: (item: ItemDto, priority: ItemPriority) => void;
  onTitleChange: (item: ItemDto, title: string) => void;
  onDelete: (itemId: number) => void;
}

const statusColors: Record<ItemStatus, string> = {
  PENDING: "bg-gray-100 text-gray-700",
  IN_PROGRESS: "bg-blue-100 text-blue-700",
  DONE: "bg-green-100 text-green-700",
};

const statusLabels: Record<ItemStatus, string> = {
  PENDING: "Pendiente",
  IN_PROGRESS: "En progreso",
  DONE: "Completada",
};

const priorityColors: Record<ItemPriority, string> = {
  LOW: "bg-gray-100 text-gray-600",
  MEDIUM: "bg-yellow-100 text-yellow-700",
  HIGH: "bg-red-100 text-red-700",
};

const priorityLabels: Record<ItemPriority, string> = {
  LOW: "Baja",
  MEDIUM: "Media",
  HIGH: "Alta",
};

export default function ItemRow({
  item,
  onStatusChange,
  onPriorityChange,
  onTitleChange,
  onDelete,
}: Props) {
  const [editing, setEditing] = useState(false);
  const [editTitle, setEditTitle] = useState(item.title);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (editing) {
      inputRef.current?.focus();
      inputRef.current?.select();
    }
  }, [editing]);

  const saveTitle = () => {
    const trimmed = editTitle.trim();
    if (trimmed && trimmed !== item.title) {
      onTitleChange(item, trimmed);
    } else {
      setEditTitle(item.title);
    }
    setEditing(false);
  };

  const cancelEdit = () => {
    setEditTitle(item.title);
    setEditing(false);
  };

  return (
    <div
      className={`flex items-center gap-3 px-4 py-3 bg-white border border-gray-200 rounded-lg group hover:shadow-sm transition-shadow ${
        item.status === "DONE" ? "opacity-60" : ""
      }`}
    >
      <GripVertical className="w-4 h-4 text-gray-300 shrink-0" />

      <input
        type="checkbox"
        checked={item.status === "DONE"}
        onChange={() =>
          onStatusChange(item, item.status === "DONE" ? "PENDING" : "DONE")
        }
        className="w-4 h-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-500 shrink-0 cursor-pointer"
      />

      {editing ? (
        <input
          ref={inputRef}
          type="text"
          value={editTitle}
          onChange={(e) => setEditTitle(e.target.value)}
          onBlur={saveTitle}
          onKeyDown={(e) => {
            if (e.key === "Enter") saveTitle();
            if (e.key === "Escape") cancelEdit();
          }}
          className="flex-1 text-sm px-1 py-0.5 border border-indigo-300 rounded focus:outline-none focus:ring-1 focus:ring-indigo-500"
        />
      ) : (
        <span
          onDoubleClick={() => setEditing(true)}
          className={`flex-1 text-sm cursor-text ${
            item.status === "DONE"
              ? "line-through text-gray-400"
              : "text-gray-900"
          }`}
          title="Doble clic para editar"
        >
          {item.title}
        </span>
      )}

      <select
        value={item.status}
        onChange={(e) => onStatusChange(item, e.target.value as ItemStatus)}
        className={`text-xs px-2 py-1 rounded-full font-medium border-0 cursor-pointer ${statusColors[item.status]}`}
      >
        {(Object.keys(statusLabels) as ItemStatus[]).map((s) => (
          <option key={s} value={s}>
            {statusLabels[s]}
          </option>
        ))}
      </select>

      <select
        value={item.priority}
        onChange={(e) =>
          onPriorityChange(item, e.target.value as ItemPriority)
        }
        className={`text-xs px-2 py-1 rounded-full font-medium border-0 cursor-pointer ${priorityColors[item.priority]}`}
      >
        {(Object.keys(priorityLabels) as ItemPriority[]).map((p) => (
          <option key={p} value={p}>
            {priorityLabels[p]}
          </option>
        ))}
      </select>

      <button
        onClick={() => item.id && onDelete(item.id)}
        className="p-1 text-gray-300 hover:text-red-500 opacity-0 group-hover:opacity-100 transition-all cursor-pointer"
        title="Eliminar item"
      >
        <Trash2 className="w-4 h-4" />
      </button>
    </div>
  );
}
