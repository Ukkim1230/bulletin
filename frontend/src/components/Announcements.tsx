"use client"

import React, { useState } from "react"
import { Card, CardContent } from "./ui/card"
import { Badge } from "./ui/badge"
import { Button } from "./ui/button"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "./ui/dialog"
import { Input } from "./ui/input"
import { Label } from "./ui/label"
import { Textarea } from "./ui/textarea"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "./ui/select"
import { Heart, Users, Calendar, ChevronDown, Plus, Pencil, Trash2, Bell } from "lucide-react"
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from "./ui/accordion"
import { Switch } from "./ui/switch"

type Announcement = {
  id: number
  title: string
  content: string
  date: string
  category: string
  icon: typeof Heart | typeof Users | typeof Calendar | typeof Bell
  urgent: boolean
}

const iconMap = {
  Heart,
  Users,
  Calendar,
  Bell,
}

const initialAnnouncements: Announcement[] = [
  {
    id: 1,
    title: "새가족 환영식",
    content: "3월 26일(일) 오후 2시 새가족을 위한 환영식이 있습니다. 많은 참여 부탁드립니다.",
    date: "2024.03.19",
    category: "행사",
    icon: Heart,
    urgent: true,
  },
  {
    id: 2,
    title: "특별새벽기도회",
    content: "부활절을 맞아 3월 25일부터 4월 9일까지 특별새벽기도회를 진행합니다.",
    date: "2024.03.18",
    category: "기도회",
    icon: Calendar,
    urgent: false,
  },
  {
    id: 3,
    title: "청년부 모임",
    content: "매주 토요일 오후 7시 청년부 모임이 있습니다. 새로운 친구들을 환영합니다.",
    date: "2024.03.17",
    category: "모임",
    icon: Users,
    urgent: false,
  },
]

export function Announcements() {
  const [announcements, setAnnouncements] = useState<Announcement[]>(initialAnnouncements)
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [editingAnnouncement, setEditingAnnouncement] = useState<Announcement | null>(null)
  const [viewingAnnouncement, setViewingAnnouncement] = useState<Announcement | null>(null)
  const [formData, setFormData] = useState({
    title: "",
    content: "",
    date: "",
    category: "행사",
    iconName: "Heart",
    urgent: false,
  })

  const handleAdd = () => {
    setEditingAnnouncement(null)
    setFormData({
      title: "",
      content: "",
      date: new Date().toISOString().split("T")[0].replace(/-/g, "."),
      category: "행사",
      iconName: "Heart",
      urgent: false,
    })
    setIsDialogOpen(true)
  }

  const handleEdit = (announcement: Announcement) => {
    setEditingAnnouncement(announcement)
    const iconName =
      Object.keys(iconMap).find((key) => iconMap[key as keyof typeof iconMap] === announcement.icon) || "Heart"
    setFormData({
      title: announcement.title,
      content: announcement.content,
      date: announcement.date,
      category: announcement.category,
      iconName,
      urgent: announcement.urgent,
    })
    setIsDialogOpen(true)
  }

  const handleDelete = (id: number) => {
    if (window.confirm("이 소식을 삭제하시겠습니까?")) {
      setAnnouncements(announcements.filter((a) => a.id !== id))
    }
  }

  const handleSave = () => {
    const icon = iconMap[formData.iconName as keyof typeof iconMap]

    if (editingAnnouncement) {
      setAnnouncements(
        announcements.map((a) =>
          a.id === editingAnnouncement.id
            ? {
                ...a,
                title: formData.title,
                content: formData.content,
                date: formData.date,
                category: formData.category,
                icon,
                urgent: formData.urgent,
              }
            : a,
        ),
      )
    } else {
      const newAnnouncement: Announcement = {
        id: Math.max(...announcements.map((a) => a.id), 0) + 1,
        title: formData.title,
        content: formData.content,
        date: formData.date,
        category: formData.category,
        icon,
        urgent: formData.urgent,
      }
      setAnnouncements([newAnnouncement, ...announcements])
    }

    setIsDialogOpen(false)
  }

  const handleCardClick = (announcement: Announcement) => {
    setViewingAnnouncement(announcement)
  }

  return (
    <section className="p-4">
      <Accordion type="single" collapsible defaultValue="announcements">
        <AccordionItem value="announcements" className="border-none">
          <AccordionTrigger className="hover:no-underline p-0 mb-4">
            <div className="flex items-center justify-between w-full">
              <h2 className="text-xl font-bold text-foreground">교회 소식</h2>
              <ChevronDown className="h-5 w-5 text-muted-foreground shrink-0 transition-transform duration-200" />
            </div>
          </AccordionTrigger>

          <AccordionContent>
            <Button onClick={handleAdd} className="w-full mb-4 bg-transparent" variant="outline">
              <Plus className="h-4 w-4 mr-2" />
              소식 추가
            </Button>

            <div className="space-y-3">
              {announcements.map((announcement) => {
                const IconComponent = announcement.icon
                return (
                  <Card
                    key={announcement.id}
                    className="border-border/50 hover:shadow-md transition-all cursor-pointer hover:scale-[1.02]"
                    onClick={() => handleCardClick(announcement)}
                  >
                    <CardContent className="p-4">
                      <div className="flex items-start gap-3">
                        <div className="flex-shrink-0 w-10 h-10 bg-primary/10 rounded-lg flex items-center justify-center">
                          <IconComponent className="h-5 w-5 text-primary" />
                        </div>

                        <div className="flex-1 min-w-0">
                          <div className="flex items-center gap-2 mb-1">
                            <h3 className="font-semibold text-foreground text-sm">{announcement.title}</h3>
                            {announcement.urgent && (
                              <Badge variant="destructive" className="text-xs px-2 py-0">
                                중요
                              </Badge>
                            )}
                            <Badge variant="secondary" className="text-xs px-2 py-0">
                              {announcement.category}
                            </Badge>
                          </div>

                          <p className="text-sm text-muted-foreground mb-2 text-pretty line-clamp-2">
                            {announcement.content}
                          </p>

                          <p className="text-xs text-muted-foreground">{announcement.date}</p>
                        </div>

                        <div
                          className="flex gap-1"
                          onClick={(e) => {
                            e.stopPropagation()
                          }}
                        >
                          <Button
                            variant="ghost"
                            size="icon"
                            className="h-8 w-8"
                            onClick={() => handleEdit(announcement)}
                          >
                            <Pencil className="h-4 w-4" />
                          </Button>
                          <Button
                            variant="ghost"
                            size="icon"
                            className="h-8 w-8 text-destructive hover:text-destructive"
                            onClick={() => handleDelete(announcement.id)}
                          >
                            <Trash2 className="h-4 w-4" />
                          </Button>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                )
              })}
            </div>
          </AccordionContent>
        </AccordionItem>
      </Accordion>

      <Dialog open={!!viewingAnnouncement} onOpenChange={() => setViewingAnnouncement(null)}>
        <DialogContent className="max-w-lg max-h-[80vh] overflow-y-auto">
          {viewingAnnouncement && (
            <>
              <DialogHeader>
                <div className="flex items-start gap-4">
                  <div className="flex-shrink-0 w-14 h-14 bg-primary/10 rounded-xl flex items-center justify-center">
                    {(() => {
                      const IconComponent = viewingAnnouncement.icon
                      return <IconComponent className="h-7 w-7 text-primary" />
                    })()}
                  </div>
                  <div className="flex-1">
                    <DialogTitle className="text-xl mb-2">{viewingAnnouncement.title}</DialogTitle>
                    <div className="flex items-center gap-2">
                      {viewingAnnouncement.urgent && (
                        <Badge variant="destructive" className="text-xs">
                          중요
                        </Badge>
                      )}
                      <Badge variant="secondary" className="text-xs">
                        {viewingAnnouncement.category}
                      </Badge>
                      <span className="text-xs text-muted-foreground">{viewingAnnouncement.date}</span>
                    </div>
                  </div>
                </div>
              </DialogHeader>

              <div className="mt-4">
                <p className="text-base text-foreground leading-relaxed whitespace-pre-wrap">
                  {viewingAnnouncement.content}
                </p>
              </div>

              <DialogFooter className="mt-6">
                <Button variant="outline" onClick={() => setViewingAnnouncement(null)} className="w-full">
                  닫기
                </Button>
              </DialogFooter>
            </>
          )}
        </DialogContent>
      </Dialog>

      {/* Edit/Add Dialog */}
      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>{editingAnnouncement ? "소식 수정" : "소식 추가"}</DialogTitle>
          </DialogHeader>

          <div className="space-y-4">
            <div>
              <Label htmlFor="title">제목</Label>
              <Input
                id="title"
                value={formData.title}
                onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                placeholder="소식 제목을 입력하세요"
              />
            </div>

            <div>
              <Label htmlFor="content">내용</Label>
              <Textarea
                id="content"
                value={formData.content}
                onChange={(e) => setFormData({ ...formData, content: e.target.value })}
                placeholder="소식 내용을 입력하세요"
                rows={4}
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="category">카테고리</Label>
                <Select
                  value={formData.category}
                  onValueChange={(value) => setFormData({ ...formData, category: value })}
                >
                  <SelectTrigger id="category">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="행사">행사</SelectItem>
                    <SelectItem value="기도회">기도회</SelectItem>
                    <SelectItem value="모임">모임</SelectItem>
                    <SelectItem value="공지">공지</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div>
                <Label htmlFor="icon">아이콘</Label>
                <Select
                  value={formData.iconName}
                  onValueChange={(value) => setFormData({ ...formData, iconName: value })}
                >
                  <SelectTrigger id="icon">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="Heart">하트</SelectItem>
                    <SelectItem value="Users">사람들</SelectItem>
                    <SelectItem value="Calendar">달력</SelectItem>
                    <SelectItem value="Bell">종</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>

            <div>
              <Label htmlFor="date">날짜</Label>
              <Input
                id="date"
                value={formData.date}
                onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                placeholder="2024.03.19"
              />
            </div>

            <div className="flex items-center justify-between">
              <Label htmlFor="urgent">중요 소식</Label>
              <Switch
                id="urgent"
                checked={formData.urgent}
                onCheckedChange={(checked) => setFormData({ ...formData, urgent: checked })}
              />
            </div>
          </div>

          <DialogFooter>
            <Button variant="outline" onClick={() => setIsDialogOpen(false)}>
              취소
            </Button>
            <Button onClick={handleSave} disabled={!formData.title || !formData.content}>
              {editingAnnouncement ? "수정" : "추가"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </section>
  )
}