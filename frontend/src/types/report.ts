export type ReportStatus = "PENDING" | "PROCESSING" | "COMPLETED" | "FAILED";

export interface ReportRequest {
  id: string;
  userId: string;
  fromDate: string;
  toDate: string;
  status: ReportStatus;
  requestedAt: string;
  downloadUrl: string | null;
}

export interface CreateReportRequest {
  fromDate: string;
  toDate: string;
}
