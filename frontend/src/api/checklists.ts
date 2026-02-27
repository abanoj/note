import api from "./axios";
import type { ChecklistCreateRequestDto, ChecklistUpdateRequestDto, ChecklistResponseDto } from "../types";

export const getChecklists = () =>
  api.get<ChecklistResponseDto[]>("/checklists");

export const getChecklist = (id: number) =>
  api.get<ChecklistResponseDto>(`/checklists/${id}`);

export const createChecklist = (data: ChecklistCreateRequestDto) =>
  api.post<ChecklistResponseDto>("/checklists", data);

export const updateChecklist = (id: number, data: ChecklistUpdateRequestDto) =>
  api.put<ChecklistResponseDto>(`/checklists/${id}`, data);

export const deleteChecklist = (id: number) =>
  api.delete(`/checklists/${id}`);
