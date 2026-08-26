export type ApplicationStatus = 'PENDING' | 'REVIEWED' | 'ACCEPTED' | 'REJECTED';

export interface AdminInternshipApplication {
  id: number;
  offerId: number;
  offerTitleEn: string;
  offerTitleAr: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  university: string | null;
  major: string | null;
  message: string | null;
  cvFileName: string;
  applicationStatus: ApplicationStatus;
  createdAt: string;
}
