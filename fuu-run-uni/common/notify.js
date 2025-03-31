// utils/notify.js
import { useNotify } from "nutui-uniapp/composables";

const notify = useNotify();

export const showPrimary = (message) => {
  notify.primary(message);
};

export const showSuccess = (message) => {
  notify.success(message);
};

export const showDanger = (message) => {
  notify.danger(message);
};

export const showWarning = (message) => {
  notify.warning(message);
};

export const hideNotify = () => {
  notify.hide();
};
