export interface DataItem {
  name: string | number | Date;
  value: string | number | Date;
  extra?: any;
  min?: number;
  max?: number;
  label?: string;
}
