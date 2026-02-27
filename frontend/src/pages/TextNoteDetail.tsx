import { useEffect, useState, useCallback } from "react";
import { useParams, Link } from "react-router-dom";
import { ArrowLeft, Loader2, Save } from "lucide-react";
import toast from "react-hot-toast";
import * as textNotesApi from "../api/textNotes";
import type { TextNoteResponseDto } from "../types";

export default function TextNoteDetail() {
  const { id } = useParams<{ id: string }>();
  const noteId = Number(id);
  const [note, setNote] = useState<TextNoteResponseDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [saving, setSaving] = useState(false);

  const fetchNote = useCallback(async () => {
    try {
      const { data } = await textNotesApi.getTextNote(noteId);
      setNote(data);
      setTitle(data.title);
      setContent(data.content ?? "");
    } catch {
      toast.error("Error al cargar la nota");
    } finally {
      setLoading(false);
    }
  }, [noteId]);

  useEffect(() => {
    fetchNote();
  }, [fetchNote]);

  const hasChanges =
    note !== null && (title !== note.title || content !== (note.content ?? ""));

  const handleSave = async () => {
    if (!note || !title.trim()) return;
    setSaving(true);
    try {
      await textNotesApi.updateTextNote(noteId, {
        id: noteId,
        title: title.trim(),
        content,
      });
      toast.success("Nota guardada");
      fetchNote();
    } catch {
      toast.error("Error al guardar la nota");
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <Loader2 className="w-6 h-6 animate-spin text-indigo-600" />
      </div>
    );
  }

  if (!note) {
    return (
      <div className="text-center py-20">
        <p className="text-gray-500">Nota no encontrada</p>
        <Link to="/" className="text-indigo-600 text-sm mt-2 inline-block">
          Volver al inicio
        </Link>
      </div>
    );
  }

  return (
    <div>
      <Link
        to="/"
        className="inline-flex items-center gap-1 text-sm text-gray-500 hover:text-indigo-600 mb-4 transition-colors"
      >
        <ArrowLeft className="w-4 h-4" />
        Volver al inicio
      </Link>

      <div className="bg-white border border-gray-200 rounded-lg p-6">
        <div className="flex items-start justify-between mb-4">
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="text-xl font-bold text-gray-900 border-0 border-b-2 border-transparent focus:border-indigo-500 focus:outline-none w-full mr-4 pb-1"
            placeholder="Titulo de la nota..."
          />
          <button
            onClick={handleSave}
            disabled={!hasChanges || !title.trim() || saving}
            className="flex items-center gap-1.5 bg-indigo-600 text-white px-3 py-1.5 rounded-md text-sm font-medium hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors shrink-0 cursor-pointer"
          >
            <Save className="w-4 h-4" />
            {saving ? "Guardando..." : "Guardar"}
          </button>
        </div>

        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="Escribe tu nota aqui..."
          className="w-full min-h-[400px] px-0 py-2 text-sm text-gray-700 border-0 focus:outline-none resize-none"
        />
      </div>
    </div>
  );
}
