import { AlertTriangle } from "lucide-react";

interface Props {
  open: boolean;
  title: string;
  message: string;
  onConfirm: () => void;
  onCancel: () => void;
}

export default function ConfirmDialog({
  open,
  title,
  message,
  onConfirm,
  onCancel,
}: Props) {
  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black/40" onClick={onCancel} />
      <div className="relative bg-white rounded-lg shadow-lg p-6 w-full max-w-sm mx-4">
        <div className="flex items-start gap-3">
          <div className="p-2 bg-red-100 rounded-full shrink-0">
            <AlertTriangle className="w-5 h-5 text-red-600" />
          </div>
          <div>
            <h3 className="text-base font-semibold text-gray-900">{title}</h3>
            <p className="mt-1 text-sm text-gray-500">{message}</p>
          </div>
        </div>
        <div className="mt-5 flex justify-end gap-2">
          <button
            onClick={onCancel}
            className="px-3 py-1.5 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 transition-colors cursor-pointer"
          >
            Cancelar
          </button>
          <button
            onClick={onConfirm}
            className="px-3 py-1.5 text-sm font-medium text-white bg-red-600 rounded-md hover:bg-red-700 transition-colors cursor-pointer"
          >
            Eliminar
          </button>
        </div>
      </div>
    </div>
  );
}
