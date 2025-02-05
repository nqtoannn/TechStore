import React from "react";
import Container from "./Container";
import nextDayDeli from "../assets/nextdaydeli.jpg"
import deliTruck from "../assets/delivery-truck.png"
import returns from "../assets/return.png"

const FooterTop = () => {
  const incentives = [
    {
      name: "Giao hàng nhanh",
      imageSrc:
        nextDayDeli,
      description:
        "Đảm bảo đơn hàng giao một cách nhanh nhất đến tay của bạn.",
    },
    {
      name: "Giao hàng toàn quốc",
      imageSrc:deliTruck,
      description:
        "Hệ thống vận chuyển đáp ứng mọi đơn hàng trong nước, thập chí là quốc tế.",
    },
    {
      name: "Đổi trả hàng miễn phí",
      imageSrc: returns,
      description:
        "Chính sách đổi trả hàng cho mọi đơn hàng với lỗi từ nhà sản xuất.",
    },
  ];

  return (
    <Container className="py-0">
      <div className="rounded-2xl bg-[#f6f6f6] px-6 py-10 sm:p-16">
        <div className="mx-auto max-w-xl lg:max-w-none">
          <div className="text-center">
            <h2 className="text-xl sm:text-2xl font-bold tracking-tight text-gray-900">
              Chúng tôi ưu tiên trải nghiệm khách hàng là hàng đầu
            </h2>
          </div>
        </div>
        <div className="mx-auto mt-12 grid max-w-sm grid-cols-1 gap-8 sm:max-w-none lg:grid-cols-3">
          {incentives.map((item) => (
            <div
              key={item.name}
              className="text-center sm:flex sm:text-left lg:block lg:text-center"
            >
              <div className="sm:flex-shrink-0">
                <div className="flex-root">
                  <img
                    src={item.imageSrc}
                    alt={item.name}
                    className="mx-auto h-16 w-16"
                  />
                </div>
              </div>
              <div className="mt-3 sm:ml-6 lg:ml-0">
                <h3 className="text-base font-medium text-gray-900">
                  {item.name}
                </h3>
                <p className="mt-2 text-sm text-gray-500">
                  {item.description}
                </p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </Container>
  );
};

export default FooterTop;
