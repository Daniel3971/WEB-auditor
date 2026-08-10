export interface InternshipOffer {
  id: number;

  titleEn: string;
  titleAr: string;

  descriptionEn: string;
  descriptionAr: string;

  missionEn: string;
  missionAr: string;

  profileEn: string;
  profileAr: string;

  duration: string;
  applicationDeadline: string;

  maxCandidates: number;
  currentCandidates: number;

  status: 'OPEN' | 'CLOSED';

  createdAt?: string;
  updatedAt?: string;
}