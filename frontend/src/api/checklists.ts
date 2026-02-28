import api from "./axios";
import type { ChecklistCreateRequestDto, ChecklistUpdateRequestDto, ChecklistResponseDto, Page } from "../types";

export const getChecklists = (page = 0, size = 10, sort = "updated,desc") =>
  api.get<Page<ChecklistResponseDto>>("/checklists", {
    params: { page, size, sort },
  });

export const getChecklist = (id: number) =>
  api.get<ChecklistResponseDto>(`/checklists/${id}`);

export const createChecklist = (data: ChecklistCreateRequestDto) =>
  api.post<ChecklistResponseDto>("/checklists", data);

export const updateChecklist = (id: number, data: ChecklistUpdateRequestDto) =>
  api.put<ChecklistResponseDto>(`/checklists/${id}`, data);

export const deleteChecklist = (id: number) =>
  api.delete(`/checklists/${id}`);
