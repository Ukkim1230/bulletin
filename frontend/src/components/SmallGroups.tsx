"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "./ui/card"
import { Button } from "./ui/button"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "./ui/dialog"
import { Input } from "./ui/input"
import { Label } from "./ui/label"
import { Textarea } from "./ui/textarea"
import { Badge } from "./ui/badge"
import { Users, MapPin, Clock, UserCircle, Plus, Pencil, Trash2, Play, ImageIcon } from "lucide-react"

interface SmallGroup {
  id: string
  name: string
  leader: string
  memberCount: number
  location: string
  time: string
  description: string
  category: string
  images: string[]
  videos: string[]
}

export function SmallGroups() {
  const [groups, setGroups] = useState<SmallGroup[]>([])

  // 초기 데이터 로드
  useEffect(() => {
    const fetchGroups = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/small-groups');
        if (response.ok) {
          const data = await response.json();
          setGroups(data);
        }
      } catch (error) {
        console.error('Error fetching small groups:', error);
      }
    };

    fetchGroups();
  }, []);

  const [selectedGroup, setSelectedGroup] = useState<SmallGroup | null>(null)
  const [isDetailOpen, setIsDetailOpen] = useState(false)
  const [isEditOpen, setIsEditOpen] = useState(false)
  const [editingGroup, setEditingGroup] = useState<SmallGroup | null>(null)
  const [formData, setFormData] = useState({
    name: "",
    leader: "",
    memberCount: 0,
    location: "",
    time: "",
    description: "",
    category: "",
    images: "",
    videos: "",
  })

  const handleAdd = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/small-groups', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          name: formData.name,
          leader: formData.leader,
          memberCount: formData.memberCount,
          location: formData.location,
          meetingTime: formData.time,
          description: formData.description,
          category: formData.category,
          images: formData.images
            .split(",")
            .map((url) => url.trim())
            .filter(Boolean)
            .join(","),
          videos: formData.videos
            .split(",")
            .map((url) => url.trim())
            .filter(Boolean)
            .join(","),
        }),
      });

      if (response.ok) {
        const newGroup = await response.json();
        setGroups([...groups, newGroup]);
        setIsEditOpen(false);
        resetForm();
      } else {
        console.error('Failed to add small group');
      }
    } catch (error) {
      console.error('Error adding small group:', error);
    }
  }

  const handleEdit = () => {
    if (!editingGroup) return
    setGroups(
      groups.map((group) =>
        group.id === editingGroup.id
          ? {
              ...group,
              name: formData.name,
              leader: formData.leader,
              memberCount: formData.memberCount,
              location: formData.location,
              time: formData.time,
              description: formData.description,
              category: formData.category,
              images: formData.images
                .split(",")
                .map((url) => url.trim())
                .filter(Boolean),
              videos: formData.videos
                .split(",")
                .map((url) => url.trim())
                .filter(Boolean),
            }
          : group,
      ),
    )
    setIsEditOpen(false)
    setEditingGroup(null)
    resetForm()
  }

  const handleDelete = (id: string) => {
    if (window.confirm("이 순모임을 삭제하시겠습니까?")) {
      setGroups(groups.filter((group) => group.id !== id))
    }
  }

  const openEditDialog = (group: SmallGroup) => {
    setEditingGroup(group)
    setFormData({
      name: group.name,
      leader: group.leader,
      memberCount: group.memberCount,
      location: group.location,
      time: group.time,
      description: group.description,
      category: group.category,
      images: group.images.join(", "),
      videos: group.videos.join(", "),
    })
    setIsEditOpen(true)
  }

  const resetForm = () => {
    setFormData({
      name: "",
      leader: "",
      memberCount: 0,
      location: "",
      time: "",
      description: "",
      category: "",
      images: "",
      videos: "",
    })
  }

  const openDetailDialog = (group: SmallGroup) => {
    setSelectedGroup(group)
    setIsDetailOpen(true)
  }

  return (
    <div className="container mx-auto px-4 py-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-foreground">순모임</h2>
          <p className="text-sm text-muted-foreground mt-1">함께 성장하는 소그룹 활동</p>
        </div>
        <Dialog open={isEditOpen} onOpenChange={setIsEditOpen}>
          <DialogTrigger asChild>
            <Button onClick={resetForm} size="sm">
              <Plus className="h-4 w-4 mr-2" />
              순모임 추가
            </Button>
          </DialogTrigger>
          <DialogContent className="max-w-md max-h-[90vh] overflow-y-auto">
            <DialogHeader>
              <DialogTitle>{editingGroup ? "순모임 수정" : "순모임 추가"}</DialogTitle>
            </DialogHeader>
            <div className="space-y-4">
              <div>
                <Label htmlFor="name">순모임 이름</Label>
                <Input
                  id="name"
                  value={formData.name}
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="예: 런닝순"
                />
              </div>
              <div>
                <Label htmlFor="leader">순장</Label>
                <Input
                  id="leader"
                  value={formData.leader}
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => setFormData({ ...formData, leader: e.target.value })}
                  placeholder="순장 이름"
                />
              </div>
              <div>
                <Label htmlFor="memberCount">순원 수</Label>
                <Input
                  id="memberCount"
                  type="number"
                  value={formData.memberCount}
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => setFormData({ ...formData, memberCount: Number.parseInt(e.target.value) || 0 })}
                  placeholder="0"
                />
              </div>
              <div>
                <Label htmlFor="location">모이는 장소</Label>
                <Input
                  id="location"
                  value={formData.location}
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => setFormData({ ...formData, location: e.target.value })}
                  placeholder="예: 한강공원"
                />
              </div>
              <div>
                <Label htmlFor="time">모이는 시간</Label>
                <Input
                  id="time"
                  value={formData.time}
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => setFormData({ ...formData, time: e.target.value })}
                  placeholder="예: 매주 토요일 오전 7시"
                />
              </div>
              <div>
                <Label htmlFor="category">카테고리</Label>
                <Input
                  id="category"
                  value={formData.category}
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) => setFormData({ ...formData, category: e.target.value })}
                  placeholder="예: 운동, 취미, 신앙"
                />
              </div>
              <div>
                <Label htmlFor="description">세부내용</Label>
                <Textarea
                  id="description"
                  value={formData.description}
                  onChange={(e: React.ChangeEvent<HTMLTextAreaElement>) => setFormData({ ...formData, description: e.target.value })}
                  placeholder="순모임에 대한 설명을 입력하세요"
                  rows={3}
                />
              </div>
              <div>
                <Label htmlFor="images">홍보 이미지 URL (쉼표로 구분)</Label>
                <Textarea
                  id="images"
                  value={formData.images}
                  onChange={(e: React.ChangeEvent<HTMLTextAreaElement>) => setFormData({ ...formData, images: e.target.value })}
                  placeholder="https://example.com/image1.jpg, https://example.com/image2.jpg"
                  rows={2}
                />
              </div>
              <div>
                <Label htmlFor="videos">홍보 영상 URL (쉼표로 구분)</Label>
                <Textarea
                  id="videos"
                  value={formData.videos}
                  onChange={(e: React.ChangeEvent<HTMLTextAreaElement>) => setFormData({ ...formData, videos: e.target.value })}
                  placeholder="https://youtube.com/watch?v=..."
                  rows={2}
                />
              </div>
              <Button onClick={editingGroup ? handleEdit : handleAdd} className="w-full">
                {editingGroup ? "수정하기" : "추가하기"}
              </Button>
            </div>
          </DialogContent>
        </Dialog>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {groups.map((group) => (
          <Card key={group.id} className="hover:shadow-lg transition-shadow">
            <CardHeader>
              <div className="flex items-start justify-between">
                <div className="flex-1">
                  <CardTitle className="text-lg">{group.name}</CardTitle>
                  <CardDescription className="mt-1">
                    <Badge variant="secondary" className="text-xs">
                      {group.category}
                    </Badge>
                  </CardDescription>
                </div>
                <div className="flex gap-1">
                  <Button variant="ghost" size="icon" onClick={() => openEditDialog(group)}>
                    <Pencil className="h-4 w-4" />
                  </Button>
                  <Button variant="ghost" size="icon" onClick={() => handleDelete(group.id)}>
                    <Trash2 className="h-4 w-4 text-destructive" />
                  </Button>
                </div>
              </div>
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="flex items-center gap-2 text-sm">
                <UserCircle className="h-4 w-4 text-muted-foreground" />
                <span className="text-muted-foreground">순장:</span>
                <span className="font-medium">{group.leader}</span>
              </div>
              <div className="flex items-center gap-2 text-sm">
                <Users className="h-4 w-4 text-muted-foreground" />
                <span className="text-muted-foreground">순원:</span>
                <span className="font-medium">{group.memberCount}명</span>
              </div>
              <div className="flex items-center gap-2 text-sm">
                <MapPin className="h-4 w-4 text-muted-foreground" />
                <span className="text-muted-foreground">장소:</span>
                <span className="font-medium text-balance">{group.location}</span>
              </div>
              <div className="flex items-center gap-2 text-sm">
                <Clock className="h-4 w-4 text-muted-foreground" />
                <span className="text-muted-foreground">시간:</span>
                <span className="font-medium text-balance">{group.time}</span>
              </div>
              <p className="text-sm text-muted-foreground line-clamp-2 text-pretty">{group.description}</p>
              <Button onClick={() => openDetailDialog(group)} className="w-full mt-2">
                가입하기
              </Button>
            </CardContent>
          </Card>
        ))}
      </div>

      <Dialog open={isDetailOpen} onOpenChange={setIsDetailOpen}>
        <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
          {selectedGroup && (
            <>
              <DialogHeader>
                <DialogTitle className="text-2xl">{selectedGroup.name}</DialogTitle>
                <Badge variant="secondary" className="w-fit">
                  {selectedGroup.category}
                </Badge>
              </DialogHeader>
              <div className="space-y-6">
                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-1">
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <UserCircle className="h-4 w-4" />
                      <span>순장</span>
                    </div>
                    <p className="font-medium">{selectedGroup.leader}</p>
                  </div>
                  <div className="space-y-1">
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <Users className="h-4 w-4" />
                      <span>순원</span>
                    </div>
                    <p className="font-medium">{selectedGroup.memberCount}명</p>
                  </div>
                  <div className="space-y-1">
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <MapPin className="h-4 w-4" />
                      <span>장소</span>
                    </div>
                    <p className="font-medium">{selectedGroup.location}</p>
                  </div>
                  <div className="space-y-1">
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <Clock className="h-4 w-4" />
                      <span>시간</span>
                    </div>
                    <p className="font-medium">{selectedGroup.time}</p>
                  </div>
                </div>

                <div className="space-y-2">
                  <h4 className="font-semibold">세부내용</h4>
                  <p className="text-sm text-muted-foreground text-pretty">{selectedGroup.description}</p>
                </div>

                {selectedGroup.images.length > 0 && (
                  <div className="space-y-3">
                    <div className="flex items-center gap-2">
                      <ImageIcon className="h-5 w-5" />
                      <h4 className="font-semibold">홍보 이미지</h4>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                      {selectedGroup.images.map((image, index) => (
                        <img
                          key={index}
                          src={image || "/placeholder.svg"}
                          alt={`${selectedGroup.name} 이미지 ${index + 1}`}
                          className="w-full h-48 object-cover rounded-lg"
                        />
                      ))}
                    </div>
                  </div>
                )}

                {selectedGroup.videos.length > 0 && (
                  <div className="space-y-3">
                    <div className="flex items-center gap-2">
                      <Play className="h-5 w-5" />
                      <h4 className="font-semibold">홍보 영상</h4>
                    </div>
                    <div className="space-y-2">
                      {selectedGroup.videos.map((video, index) => (
                        <a
                          key={index}
                          href={video}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="flex items-center gap-2 text-sm text-primary hover:underline"
                        >
                          <Play className="h-4 w-4" />
                          영상 {index + 1} 보기
                        </a>
                      ))}
                    </div>
                  </div>
                )}

                <Button className="w-full" size="lg">
                  가입 신청하기
                </Button>
              </div>
            </>
          )}
        </DialogContent>
      </Dialog>
    </div>
  )
}
