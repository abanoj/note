import api from "./axios";
import type { TextNoteCreateRequestDto, TextNoteUpdateRequestDto, TextNoteResponseDto } from "../types";

export const getTextNotes = () =>
  api.get<TextNoteResponseDto[]>("/text-notes");

export const getTextNote = (id: number) =>
  api.get<TextNoteResponseDto>(`/text-notes/${id}`);

export const createTextNote = (data: TextNoteCreateRequestDto) =>
  api.post<TextNoteResponseDto>("/text-notes", data);

export const updateTextNote = (id: number, data: TextNoteUpdateRequestDto) =>
  api.put<TextNoteResponseDto>(`/text-notes/${id}`, data);

export const deleteTextNote = (id: number) =>
  api.delete(`/text-notes/${id}`);
